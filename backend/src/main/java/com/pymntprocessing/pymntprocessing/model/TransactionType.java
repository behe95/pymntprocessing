package com.pymntprocessing.pymntprocessing.model;


import jakarta.persistence.*;

@Entity
@Table(name = "TransactionType")
public class TransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionTypePk")
    private Long id;

    private String name;

    private int multiplier;

    private int code;

    public TransactionType() {
    }

    public TransactionType(Long id, String name, int multiplier, int code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
