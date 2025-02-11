package com.example.finv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Income> incomes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Yearly> yearlies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Monthly> monthlies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Weekly> weeklies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Daily> dailies;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Balance balance;

}
