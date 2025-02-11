package com.example.finv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Yearly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String year;
    private double amount;

    @OneToMany(mappedBy = "yearly")
    private List<Monthly> monthlies;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Constructor with User and year
    public Yearly(User user, String year) {
        this.user = user;
        this.year = year;
    }

    // Default constructor for JPA
    public Yearly() {}
}
