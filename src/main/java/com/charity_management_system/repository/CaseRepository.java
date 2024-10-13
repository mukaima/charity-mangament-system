package com.charity_management_system.repository;

import com.charity_management_system.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Integer> {

    List<Case> findAllByTitle(String title);
    List<Case> findAllByUserUsername(String username);
    List<Case> findAllByCategoryId(int categoryId);
    List<Case> findByTitleContainingOrDescriptionContaining(String title, String description);
}
