package com.example.finv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Weekly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String week;
    private double amount;

    @OneToMany(mappedBy = "weekly")
    private List<Daily> dailies;

    @ManyToOne
    @JoinColumn(name = "monthly_id")
    @JsonIgnore
    private Monthly monthly;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Add a constructor with User and week
    public Weekly(User user, String week) {
        this.user = user;
        this.week = week;
    }

    // Default constructor for JPA
    public Weekly() {}
}
