package com.charity_management_system.dto;

import com.charity_management_system.enums.PaymentMethod;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonationDto {


    private double amount;
    private PaymentMethod paymentMethod;
    private Integer caseId;
}
