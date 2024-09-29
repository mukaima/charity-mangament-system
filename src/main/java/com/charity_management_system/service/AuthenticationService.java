package com.charity_management_system.service;

import com.charity_management_system.dto.LoginRequest;
import com.charity_management_system.dto.LoginResponse;
import com.charity_management_system.model.User;

public interface AuthenticationService {

    String register(User user);
    LoginResponse login(LoginRequest loginRequest);
    Boolean checkUsername(String username);
    Boolean checkEmail(String email);
}
