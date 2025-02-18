package com.example.finv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Weekly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String week;
    private double amount;

    @OneToMany(mappedBy = "weekly", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Daily> dailies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "monthly_id")
    @JsonIgnore
    private Monthly monthly;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Default constructor for JPA
    public Weekly() {}

    // Constructor with User and week
    public Weekly(User user, String week) {
        this.user = user;
        this.week = week;
    }

    public void addDaily(Daily daily) {
        this.dailies.add(daily);
        daily.setWeekly(this);  // Ensure bidirectional consistency
    }
}