package com.charity_management_system.service.impl;

import com.charity_management_system.dto.DonationDto;
import com.charity_management_system.exception.custom.CaseNotFoundException;
import com.charity_management_system.exception.custom.UserNotFoundException;
import com.charity_management_system.model.Case;
import com.charity_management_system.model.Donation;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.CaseRepository;
import com.charity_management_system.repository.DonationRepository;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;
    private final CommonService commonService;

    /**
     * Retrieves all donations made to a specific case.
     *
     * @param caseId The ID of the case.
     * @return A list of Donation entities.
     */
    @Override
    public List<Donation> getDonationsByCaseId(int caseId) {
        Case theCase = caseRepository.findById(caseId).orElseThrow(() -> new CaseNotFoundException("Case Not Found With Id: " + caseId));
        return theCase.getDonations();
    }

    /**
     * Creates a new donation for a case.
     *
     * @param donationDTO The details of the donation.
     * @return The created Donation entity.
     */
    @Override
    public Donation makeDonation(DonationDto donationDTO) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User caseUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));

        int donationCaseId = donationDTO.getCaseId();
        Case donationCase = caseRepository.findById(donationCaseId).orElseThrow(() -> new CaseNotFoundException("case not found"));

        double raise = donationCase.getAmountRaised() + donationDTO.getAmount();
        donationCase.setAmountRaised(raise);

        Donation donation = new Donation();
        donation.setAmount(donationDTO.getAmount());
        donation.setPaymentMethod(donationDTO.getPaymentMethod());
        donation.setCaseEntity(donationCase);
        donation.setUser(caseUser);

        donationCase.getDonations().add(donation);


        return donationRepository.save(donation);
    }

    /**
     * Retrieves all donations made by a specific user.
     *
     * @param username The username of the user.
     * @return A list of DonationDto objects representing the user's donations.
     */
    @Override
    public List<DonationDto> getUserDonationsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found"));

        List<Donation> donations = user.getDonations();
        List<DonationDto> donationDtos = new ArrayList<>();

        for (Donation donation: donations) {
            DonationDto donationDTO = commonService.convertDonationToDonationDTO(donation);
            donationDtos.add(donationDTO);
        }
        return donationDtos;
    }
}
