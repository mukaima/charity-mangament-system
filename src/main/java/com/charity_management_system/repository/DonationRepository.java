package com.charity_management_system.repository;

import com.charity_management_system.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Integer> {

    List<Donation> findByCaseEntityId(int caseId);
    List<Donation> findAllByUserUsername(String username);
}
