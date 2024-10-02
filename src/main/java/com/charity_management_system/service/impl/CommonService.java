package com.charity_management_system.service.impl;

import com.charity_management_system.dto.CaseDto;
import com.charity_management_system.dto.DonationDto;
import com.charity_management_system.model.Case;
import com.charity_management_system.model.Donation;
import org.springframework.stereotype.Service;

/**
 * Common utility service for converting entities to DTOs.
 */
@Service
public class CommonService {

    /**
     * Converts a Case entity to a CaseDto.
     *
     * @param c The Case entity to convert.
     * @return A CaseDto representing the case.
     */
    public CaseDto convertCaseToCaseDTO(Case c){
        CaseDto caseDTO = new CaseDto();
        caseDTO.setId(c.getId());
        caseDTO.setTitle(c.getTitle());
        caseDTO.setGoal(c.getGoal());
        caseDTO.setDescription(c.getDescription());
        caseDTO.setImagePath(c.getImagePath());
        caseDTO.setAmountRaised(c.getAmountRaised());
        return caseDTO;
    }

    /**
     * Converts a Donation entity to a DonationDto.
     *
     * @param d The Donation entity to convert.
     * @return A DonationDto representing the donation.
     */
    public DonationDto convertDonationToDonationDTO(Donation d){
        DonationDto donationDTO = new DonationDto();
        donationDTO.setAmount(d.getAmount());
        donationDTO.setPaymentMethod(d.getPaymentMethod());
        donationDTO.setCaseId(d.getCaseEntity().getId());
        return donationDTO;
    }
}
