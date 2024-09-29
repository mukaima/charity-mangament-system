package com.charity_management_system.service;

import com.charity_management_system.dto.DonationDTO;
import com.charity_management_system.model.Donation;

import java.util.List;

public interface DonationService {

    List<Donation> getDonationByCaseId(int caseId);
    Donation makeDonation(DonationDTO donation);
    List<DonationDTO> getUserDonationsByUsername(String username);
}
