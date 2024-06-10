package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.PaymentTransaction;
//import com.pymntprocessing.pymntprocessing.model.TransactionStatus;
import com.pymntprocessing.pymntprocessing.model.TransactionType;

import java.util.List;

public interface PaymentTransactionService {

    public PaymentTransaction getPaymentTransactionById(Long id);
    public List<PaymentTransaction> getAllPaymentTransaction();

    public List<PaymentTransaction> getAllPaymentTransactionByVendorId(Long id);

    public PaymentTransaction createPaymentTransaction(PaymentTransaction paymentTransaction);

//    public TransactionStatus getTransactionStatusById(Long id);

    public TransactionType getTransactionTypeById(Long id);


    public PaymentTransaction updatePaymentTransaction(Long id, PaymentTransaction paymentTransaction);


    public void deletePaymentTransaction(Long id);

}
