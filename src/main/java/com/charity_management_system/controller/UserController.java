package com.charity_management_system.controller;

import com.charity_management_system.dto.UserProfile;
import com.charity_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * gets a user profile containing the user's cases and donations
     * @return UserProfile object containing the cases and the donations
     */
    @GetMapping("/account")
    public ResponseEntity<UserProfile> getUserAccount(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserAccount(username));
    }
}
