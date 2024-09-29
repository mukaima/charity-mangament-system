package com.charity_management_system.dto;

import com.charity_management_system.model.Case;
import com.charity_management_system.model.Donation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    private String username;
    private String email;
    private List<Case> caseList;
    private List<Donation> donations;
}
