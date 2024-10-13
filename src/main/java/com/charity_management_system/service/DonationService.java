package com.charity_management_system.service;

import com.charity_management_system.dto.DonationDto;
import com.charity_management_system.model.Donation;

import java.util.List;

public interface DonationService {

    List<Donation> getDonationsByCaseId(int caseId);
    Donation makeDonation(DonationDto donation);
    List<DonationDto> getUserDonationsByUsername(String username);
}
