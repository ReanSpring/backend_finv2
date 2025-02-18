package com.example.finv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Newthing {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate date;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


}
