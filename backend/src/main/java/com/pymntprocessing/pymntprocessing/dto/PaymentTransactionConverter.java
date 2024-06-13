package com.pymntprocessing.pymntprocessing.dto;

import com.pymntprocessing.pymntprocessing.entity.Invoice;
import com.pymntprocessing.pymntprocessing.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentTransactionConverter {
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentTransactionConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PaymentTransactionDTO toDTO(PaymentTransaction paymentTransaction) {
        PaymentTransactionDTO paymentTransactionDTO = null;
        if (paymentTransaction != null) {
            paymentTransactionDTO = this.modelMapper.map(paymentTransaction, PaymentTransactionDTO.class);
            if (paymentTransaction.getInvoice() != null) {
                paymentTransactionDTO.setInvoiceDTO(this.modelMapper.map(paymentTransaction.getInvoice(), InvoiceDTO.class));
            }

            if (paymentTransaction.getProduct() != null) {
                paymentTransactionDTO.setProductDTO(this.modelMapper.map(paymentTransaction.getProduct(), ProductDTO.class));
            }
        }
        return paymentTransactionDTO;
    }

    public PaymentTransaction toEntity(PaymentTransactionDTO paymentTransactionDTO) {
        PaymentTransaction paymentTransaction = null;

        if (paymentTransactionDTO != null) {
            paymentTransaction = this.modelMapper.map(paymentTransactionDTO, PaymentTransaction.class);
            if (paymentTransactionDTO.getInvoiceDTO() != null) {
                paymentTransaction.setInvoice(this.modelMapper.map(paymentTransactionDTO.getInvoiceDTO(), Invoice.class));
            }

            if (paymentTransactionDTO.getProductDTO() != null) {
                paymentTransaction.setProduct(this.modelMapper.map(paymentTransactionDTO.getProductDTO(), Product.class));
            }
        }
        return paymentTransaction;
    }
}