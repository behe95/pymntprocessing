package com.pymntprocessing.pymntprocessing.model.dto;

import java.time.LocalDateTime;

public class ProductDTO {
    private Long id;

    private PaymentTransactionDTO paymentTransactionDTO;

    private String productName;

    private String productDescription;

    private LocalDateTime created;
    private LocalDateTime modified;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentTransactionDTO getPaymentTransactionDTO() {
        return paymentTransactionDTO;
    }

    public void setPaymentTransactionDTO(PaymentTransactionDTO paymentTransactionDTO) {
        this.paymentTransactionDTO = paymentTransactionDTO;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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
