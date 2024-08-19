package com.charity.charity_management_system.model;

import com.charity.charity_management_system.enums.CaseCategory;
import com.charity.charity_management_system.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private CaseCategory caseCategory;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "goal")
    private double goal;

    @Column(name = "amount_raised")
    private double amountRaised;

    @Column(name = "status")
    private CaseStatus caseStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

