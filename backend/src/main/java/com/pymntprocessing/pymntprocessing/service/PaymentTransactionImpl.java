package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService{

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    public PaymentTransactionImpl(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Override
    public PaymentTransaction getPaymentTransactionById(Long id) {
        Optional<PaymentTransaction> paymentTransaction = this.paymentTransactionRepository.findById(id);
        return paymentTransaction.orElse(null);
    }

    @Override
    public List<PaymentTransaction> getAllPaymentTransaction() {
        return this.paymentTransactionRepository.findAll();
    }

    @Override
    public List<PaymentTransaction> getAllPaymentTransactionByVendorId(Long id) {
        return this.paymentTransactionRepository.findAllByVendorId(id);
    }
}
