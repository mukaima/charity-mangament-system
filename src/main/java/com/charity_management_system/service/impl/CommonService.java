package com.charity_management_system.service.impl;

import com.charity_management_system.dto.CaseDTO;
import com.charity_management_system.dto.DonationDTO;
import com.charity_management_system.model.Case;
import com.charity_management_system.model.Donation;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    public CaseDTO convertCaseToCaseDTO(Case c){
        CaseDTO caseDTO = new CaseDTO();
        caseDTO.setId(c.getId());
        caseDTO.setTitle(c.getTitle());
        caseDTO.setGoal(c.getGoal());
        caseDTO.setDescription(c.getDescription());
        caseDTO.setImagePath(c.getImagePath());
        caseDTO.setAmountRaised(c.getAmountRaised());
        return caseDTO;
    }

    public DonationDTO convertDonationToDonationDTO(Donation d){
        DonationDTO donationDTO = new DonationDTO();
        donationDTO.setAmount(d.getAmount());
        donationDTO.setPaymentMethod(d.getPaymentMethod());
        donationDTO.setCaseId(d.getCaseEntity().getId());
        return donationDTO;
    }
}
