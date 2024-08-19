package com.charity.charity_management_system.model;

import com.charity.charity_management_system.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "amount")
    private int amount;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case aCase;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
