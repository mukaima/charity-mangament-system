package com.charity_management_system.service.impl;

import com.charity_management_system.dto.UserProfile;
import com.charity_management_system.exception.custom.UserNotFoundException;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Retrieves the user profile by username.
     *
     * @param username The username of the user.
     * @return The UserProfile object representing the user's account.
     */
    public UserProfile getUserAccount(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User Not Found With Username: " + username));

        return new UserProfile(user.getUsername(), user.getEmail(), user.getCaseList(), user.getDonations());
    }
}
