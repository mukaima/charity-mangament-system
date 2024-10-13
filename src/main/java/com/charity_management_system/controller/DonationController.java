package com.charity_management_system.controller;

import com.charity_management_system.dto.DonationDto;
import com.charity_management_system.model.Donation;
import com.charity_management_system.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    /**
     * gets all the donations of a specific case by its id
     * @param caseId the id of the case
     * @return List of the donations that were made to that case
     */
    @GetMapping("/getByCaseId/{caseId}")
    public ResponseEntity<List<Donation>> getDonationByCaseId(@PathVariable int caseId){
        return ResponseEntity.ok(donationService.getDonationsByCaseId(caseId));
    }

    /**
     * makes a new donation to a case
     * @param donation the donation dto object containing the case id
     * @return the donation object
     */
    @PostMapping("/makeDonation")
    public ResponseEntity<Donation> makeDonation(@RequestBody DonationDto donation){
        return ResponseEntity.ok(donationService.makeDonation(donation));
    }

    /**
     * gets all the donations that the user made by the user's username
     * @param username the username of the user
     * @return list of all the donations that the user made
     */
    @GetMapping("/me")
    public ResponseEntity<List<DonationDto>> getDonationByUsername(@RequestParam String username){
        return ResponseEntity.ok(donationService.getUserDonationsByUsername(username));
    }
}
