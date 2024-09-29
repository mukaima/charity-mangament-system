package com.charity_management_system.service.impl;

import com.charity_management_system.dto.DonationDTO;
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

    @Override
    public List<Donation> getDonationByCaseId(int caseId) {
        return donationRepository.findByCaseEntityId(caseId);
    }

    @Override
    public Donation makeDonation(DonationDTO donationDTO) {

        // fetching the currently authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User caseUser = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found"));

        int donationCaseId = donationDTO.getCaseId();
        Case donationCase = caseRepository.findById(donationCaseId).orElseThrow(() -> new RuntimeException("case not found"));

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

    @Override
    public List<DonationDTO> getUserDonationsByUsername(String username) {


        List<Donation> donations = donationRepository.findAllByUserUsername(username);
        List<DonationDTO> donationDTOS = new ArrayList<>();

        for (Donation donation: donations) {
            DonationDTO donationDTO = commonService.convertDonationToDonationDTO(donation);
            donationDTOS.add(donationDTO);
        }
        return donationDTOS;
    }
}
