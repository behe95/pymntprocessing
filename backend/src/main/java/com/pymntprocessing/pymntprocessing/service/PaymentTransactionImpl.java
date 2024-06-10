package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.TransactionType;
import com.pymntprocessing.pymntprocessing.model.Vendor;
import com.pymntprocessing.pymntprocessing.repository.PaymentTransactionRepository;
import com.pymntprocessing.pymntprocessing.repository.TransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService{

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    @Autowired
    public PaymentTransactionImpl(PaymentTransactionRepository paymentTransactionRepository, TransactionTypeRepository transactionTypeRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.transactionTypeRepository = transactionTypeRepository;
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

    @Override
    public PaymentTransaction createPaymentTransaction(PaymentTransaction paymentTransaction) {
        return this.paymentTransactionRepository.save(paymentTransaction);
    }


    @Override
    public TransactionType getTransactionTypeById(Long id) {
        Optional<TransactionType> transactionType = this.transactionTypeRepository.findById(id);
        return transactionType.orElse(null);
    }

    @Override
    public PaymentTransaction updatePaymentTransaction(Long id, PaymentTransaction paymentTransaction) {

        Optional<PaymentTransaction> existingPaymentTransaction = this.paymentTransactionRepository.findById(id);
        if (existingPaymentTransaction.isPresent()) {
            return this.paymentTransactionRepository.save(paymentTransaction);
        }
        return null;
    }

    @Override
    public void deletePaymentTransaction(Long id) {
        this.paymentTransactionRepository.deleteById(id);
    }
}
