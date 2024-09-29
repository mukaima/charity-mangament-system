package com.charity_management_system.service.impl;

import com.charity_management_system.config.JwtService;
import com.charity_management_system.dto.LoginRequest;
import com.charity_management_system.dto.LoginResponse;
import com.charity_management_system.dto.UserProfile;
import com.charity_management_system.enums.Role;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(User user) {
        User savedUser = null;
        String response = null;
        try {
             String pwdHash = passwordEncoder.encode(user.getPassword());
             user.setPassword(pwdHash);
             user.setRole(Role.valueOf("REGULAR_USER"));
             savedUser = userRepository.save(user);
            if (savedUser.getId() != null){
                response = "saved user successfully";
            }
        }catch (Exception ex){
            response = ex.getMessage();
        }
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Retrieve the user from the repository after successful authentication
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found!"));

            // Create a UserProfile object
            UserProfile userProfile = new UserProfile(user.getUsername(), user.getEmail(), user.getCaseList(), user.getDonations());

            // Generate a JWT token
            String token = jwtService.generateToken(user);

            return new LoginResponse(token, jwtService.getExpirationTime(), userProfile);

        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public Boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
