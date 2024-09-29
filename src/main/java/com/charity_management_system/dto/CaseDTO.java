package com.charity_management_system.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseDTO {

    private int id;
    private String title;
    private String description;
    private String imagePath;
    private double goal;
    private double amountRaised;
}
