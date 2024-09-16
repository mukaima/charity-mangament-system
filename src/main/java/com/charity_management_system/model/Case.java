package com.charity_management_system.model;

import com.charity_management_system.enums.CaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "goal")
    private double goal;

    @Column(name = "amount_raised")
    private double amountRaised;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private CaseStatus caseStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "caseEntity")
    @JsonIgnoreProperties("caseEntity")
    private List<Donation> donations;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"caseList", "donations"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("caseList")
    private Category category;
}

