package com.pymntprocessing.pymntprocessing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PaymentTransaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentTransactionPk")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fkVendor", referencedColumnName = "vendorPk")
    private Vendor vendor;

//    @OneToOne
//    @JoinColumn(name = "fkTransactionStatus", referencedColumnName = "transactionStatusPk")
//    private TransactionStatus transactionStatus;

    @OneToOne
    @JoinColumn(name = "fkTransactionType", referencedColumnName = "transactionTypePk")
    private TransactionType transactionType;

    private int transactionNumber;

    private String transactionDescription;

    private Double transactionAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }


//    public TransactionStatus getTransactionStatus() {
//        return transactionStatus;
//    }
//
//    public void setTransactionStatus(TransactionStatus transactionStatus) {
//        this.transactionStatus = transactionStatus;
//    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(int transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
