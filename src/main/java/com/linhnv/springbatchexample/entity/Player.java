package com.linhnv.springbatchexample.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity(name = "player")
public class Player implements Serializable {

    @Id
    private String id;

    @Column
    private String lastName;

    @Column
    private String firstName;

    @Column
    private String position;

    @Column
    private int birthYear;

    @Column
    private int debutYear;
}