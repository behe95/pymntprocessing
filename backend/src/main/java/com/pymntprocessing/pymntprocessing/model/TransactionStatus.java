package com.pymntprocessing.pymntprocessing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TransactionStatus")
public class TransactionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionStatusPk")
    private Long id;

    private String name;

    private int code;

    public TransactionStatus() {
    }

    public TransactionStatus(Long id, String name, int code) {
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
