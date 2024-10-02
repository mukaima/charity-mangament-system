package com.charity_management_system.controller;

import com.charity_management_system.dto.LoginRequest;
import com.charity_management_system.dto.LoginResponse;
import com.charity_management_system.model.User;
import com.charity_management_system.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user.
     *
     * @param user The user to register.
     * @return A success message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user){
        return ResponseEntity.ok(authenticationService.register(user));
    }

    /**
     * Logs in the user and returns a token if successful.
     *
     * @param loginRequest The login request containing username and password.
     * @return The login response containing the JWT token and user profile.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if a username is already taken.
     *
     * @param username The username to check.
     * @return True if the username is available, false otherwise.
     */
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username){
        return ResponseEntity.ok(authenticationService.checkUsername(username));
    }

    /**
     * Checks if an email is already registered.
     *
     * @param email The email to check.
     * @return True if the email is available, false otherwise.
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email){
        return ResponseEntity.ok(authenticationService.checkEmail(email));
    }
}
