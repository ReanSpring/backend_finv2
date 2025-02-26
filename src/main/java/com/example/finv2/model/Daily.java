package com.example.finv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Daily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private double amount;
    private String source;
    private LocalDate date;
    @Transient
    private String formattedDate;

    @ManyToOne
    @JoinColumn(name = "weekly_id")
    @JsonIgnore
    private Weekly weekly;

    @ManyToOne
    @JoinColumn(name = "monthly_id")
    @JsonIgnore
    private Monthly monthly;

    @ManyToOne
    @JoinColumn(name = "yearly_id")
    @JsonIgnore
    private Yearly yearly;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Default constructor
    public Daily() {}

    // Constructor for easier instantiation
    public Daily(User user, LocalDate date, double amount, String source, String day) {
        this.user = user;
        this.date = date;
        this.amount = amount;
        this.source = source;
        this.day = day;
    }
}
