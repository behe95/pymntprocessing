package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.PaymentTransaction;

import java.util.List;

public interface PaymentTransactionService {

    public PaymentTransaction getPaymentTransactionById(Long id);
    public List<PaymentTransaction> getAllPaymentTransaction();

    public List<PaymentTransaction> getAllPaymentTransactionByVendorId(Long id);

}
