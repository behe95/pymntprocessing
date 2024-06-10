package com.pymntprocessing.pymntprocessing.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoicePk")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fkVendor", referencedColumnName = "vendorPk")
    private Vendor vendor;


    @OneToOne
    @JoinColumn(name = "fkInvoiceStatus", referencedColumnName = "invoiceStatusPk")
    private InvoiceStatus invoiceStatus;


    @ManyToOne
    @JoinColumn(name = "fkPaymentTransaction", referencedColumnName = "paymentTransactionPk")
    private PaymentTransaction paymentTransaction;

    private int invoiceNumber;

    private String invoiceDescription;

    private Double invoiceAmount;

    private LocalDateTime invoiceDate;
    private LocalDateTime invoiceDueDate;
    private LocalDateTime invoiceReceivedDate;
    private LocalDateTime created;
    private LocalDateTime modified;

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

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDateTime getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public void setInvoiceDueDate(LocalDateTime invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    public LocalDateTime getInvoiceReceivedDate() {
        return invoiceReceivedDate;
    }

    public void setInvoiceReceivedDate(LocalDateTime invoiceReceivedDate) {
        this.invoiceReceivedDate = invoiceReceivedDate;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
}
