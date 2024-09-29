package com.charity_management_system.controller;

import com.charity_management_system.dto.DonationDTO;
import com.charity_management_system.model.Donation;
import com.charity_management_system.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @GetMapping("/getByCaseId/{caseId}")
    public ResponseEntity<List<Donation>> getDonationByCaseId(@PathVariable int caseId){
        return ResponseEntity.ok(donationService.getDonationByCaseId(caseId));
    }

    @PostMapping("/makeDonation")
    public ResponseEntity<Donation> makeDonation(@RequestBody DonationDTO donation){
        return ResponseEntity.ok(donationService.makeDonation(donation));
    }

    @GetMapping("/me")
    public ResponseEntity<List<DonationDTO>> getDonationByUsername(@RequestParam String username){
        return ResponseEntity.ok(donationService.getUserDonationsByUsername(username));
    }
}
