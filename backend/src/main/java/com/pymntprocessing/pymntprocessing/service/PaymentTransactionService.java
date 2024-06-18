package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;

import java.util.List;

public interface PaymentTransactionService {

    public PaymentTransactionDTO getPaymentTransactionById(Long id);
    public List<PaymentTransactionDTO> getAllPaymentTransaction();

    public List<PaymentTransactionDTO> getAllPaymentTransactionByVendorId(Long id);

    public PaymentTransactionDTO createPaymentTransaction(PaymentTransactionDTO paymentTransactionDTO);

    public TransactionType getTransactionTypeById(Long id);


    public PaymentTransactionDTO updatePaymentTransaction(Long id, PaymentTransactionDTO paymentTransactionDTO);


    public void deletePaymentTransaction(Long id);

    public List<PaymentTransactionDTO> getAllPaymentTransactionsByIds(List<Long> ids);

}
