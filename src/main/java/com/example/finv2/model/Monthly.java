package com.example.finv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Monthly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String month;
    private double amount;
    private String year;

    @OneToMany(mappedBy = "monthly")
    @JsonIgnore
    private List<Weekly> weeklies;

    @OneToMany(mappedBy = "monthly")
    private List<Daily> dailies;

    @ManyToOne
    @JoinColumn(name = "yearly_id")
    @JsonIgnore
    private Yearly yearly;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Constructor with User, month, and year
    public Monthly(User user, String month, String year) {
        this.user = user;
        this.month = month;
        this.year = year;
    }

    // Default constructor for JPA
    public Monthly() {}
}
