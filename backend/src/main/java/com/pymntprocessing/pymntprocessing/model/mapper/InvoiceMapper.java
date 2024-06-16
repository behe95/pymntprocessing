package com.pymntprocessing.pymntprocessing.model.mapper;

import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Invoice;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class InvoiceMapper extends BaseMapper<Invoice, InvoiceDTO> {
    private final ModelMapper modelMapper;

    @Autowired
    public InvoiceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public Invoice convertToEntity(InvoiceDTO dto) {
        Invoice invoice = null;

        if (dto != null) {
            invoice = this.modelMapper.map(dto, Invoice.class);
            if (dto.getPaymentTransactionDTO() != null) {
                List<PaymentTransaction> paymentTransactions = dto.getPaymentTransactionDTO().stream().map(paymentTransactionDTO -> this.modelMapper.map(paymentTransactionDTO, PaymentTransaction.class)).toList();
                invoice.setPaymentTransaction(paymentTransactions);
            }
        }

        return invoice;
    }

    @Override
    public InvoiceDTO convertToDTO(Invoice entity) {
        InvoiceDTO invoiceDTO = null;

        if (entity != null) {
            invoiceDTO = this.modelMapper.map(entity, InvoiceDTO.class);
            if (entity.getPaymentTransaction() != null) {
                List<PaymentTransactionDTO> paymentTransactionDTOS = entity.getPaymentTransaction().stream().map(paymentTransaction -> this.modelMapper.map(paymentTransaction, PaymentTransactionDTO.class)).toList();
                invoiceDTO.setPaymentTransactionDTO(paymentTransactionDTOS);
            }
        }

        return invoiceDTO;
    }
}
