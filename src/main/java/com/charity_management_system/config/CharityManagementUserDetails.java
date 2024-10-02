package com.charity_management_system.config;

import com.charity_management_system.model.User;
import com.charity_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CharityManagementUserDetails implements UserDetailsService {

    private final UserRepository userRepository;


    /**
     * Loads a user by their username for authentication.
     *
     * @param username The username of the user to load.
     * @return A UserDetails object containing user information.
     * @throws UsernameNotFoundException If the user is not found in the repository.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User Not found with this username : " + username));

        return new org.springframework.security.core.userdetails.User
                (user.getUsername(), user.getPassword(), getAuthority(user));
    }

    /**
     * Retrieves the granted authorities for a user based on their role.
     *
     * @param user The user whose authorities are being retrieved.
     * @return A set of SimpleGrantedAuthority objects representing the user's authorities.
     */
    private Set<SimpleGrantedAuthority> getAuthority(User user){

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return authorities;
    }
}
