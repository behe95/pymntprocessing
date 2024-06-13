package com.pymntprocessing.pymntprocessing.dto;

import com.pymntprocessing.pymntprocessing.entity.Invoice;
import com.pymntprocessing.pymntprocessing.entity.PaymentTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class InvoiceConverter {
    private final ModelMapper modelMapper;

    @Autowired
    public InvoiceConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public InvoiceDTO toDTO(Invoice invoice) {
        InvoiceDTO invoiceDTO = null;

        if (invoice != null) {
            invoiceDTO = this.modelMapper.map(invoice, InvoiceDTO.class);
            if (invoice.getPaymentTransaction() != null) {
                List<PaymentTransactionDTO> paymentTransactionDTOS = invoice.getPaymentTransaction().stream().map(paymentTransaction -> this.modelMapper.map(paymentTransaction, PaymentTransactionDTO.class)).toList();
                invoiceDTO.setPaymentTransactionDTO(paymentTransactionDTOS);
            }
        }

        return invoiceDTO;
    }

    public Invoice toEntity(InvoiceDTO invoiceDTO) {
        Invoice invoice = null;

        if (invoiceDTO != null) {
            invoice = this.modelMapper.map(invoiceDTO, Invoice.class);
            if (invoiceDTO.getPaymentTransactionDTO() != null) {
                List<PaymentTransaction> paymentTransactions = invoiceDTO.getPaymentTransactionDTO().stream().map(paymentTransactionDTO -> this.modelMapper.map(paymentTransactionDTO, PaymentTransaction.class)).toList();
                invoice.setPaymentTransaction(paymentTransactions);
            }
        }

        return invoice;
    }
}
