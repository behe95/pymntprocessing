package com.pymntprocessing.pymntprocessing.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "InvoiceStatus")
public class InvoiceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoiceStatusPk")
    private Long id;

    private String name;

    private int code;

    public InvoiceStatus() {
    }

    public InvoiceStatus(Long id, String name, int code) {
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
