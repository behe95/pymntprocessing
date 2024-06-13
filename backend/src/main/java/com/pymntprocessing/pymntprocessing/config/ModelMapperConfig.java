package com.pymntprocessing.pymntprocessing.config;

import com.pymntprocessing.pymntprocessing.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.entity.Invoice;
import com.pymntprocessing.pymntprocessing.entity.PaymentTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Invoice.class, InvoiceDTO.class).addMappings(configurableConditionExpression -> {
            configurableConditionExpression.skip(Invoice::getPaymentTransaction, InvoiceDTO::setPaymentTransactionDTO);
        });

        modelMapper.createTypeMap(PaymentTransaction.class, PaymentTransactionDTO.class).addMappings(configurableConditionExpression -> {
            configurableConditionExpression.skip(PaymentTransaction::getInvoice, PaymentTransactionDTO::setInvoiceDTO);
        });


        return modelMapper;
    }
}
