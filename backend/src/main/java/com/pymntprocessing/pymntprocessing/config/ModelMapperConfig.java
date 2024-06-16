package com.pymntprocessing.pymntprocessing.config;

import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Invoice;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
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
            configurableConditionExpression.skip(PaymentTransaction::getProduct, PaymentTransactionDTO::setProductDTO);
        });


        return modelMapper;
    }
}
