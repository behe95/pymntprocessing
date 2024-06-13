package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.dto.PaymentTransactionConverter;
import com.pymntprocessing.pymntprocessing.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.repository.PaymentTransactionRepository;
import com.pymntprocessing.pymntprocessing.repository.TransactionTypeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentTransactionImpl implements PaymentTransactionService{

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    private final ModelMapper modelMapper;

    private final PaymentTransactionConverter paymentTransactionConverter;

    @Autowired
    public PaymentTransactionImpl(PaymentTransactionRepository paymentTransactionRepository, TransactionTypeRepository transactionTypeRepository, ModelMapper modelMapper, PaymentTransactionConverter paymentTransactionConverter) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.transactionTypeRepository = transactionTypeRepository;
        this.modelMapper = modelMapper;
        this.paymentTransactionConverter = paymentTransactionConverter;
    }

    @Override
    public PaymentTransactionDTO getPaymentTransactionById(Long id) {
        Optional<PaymentTransaction> paymentTransaction = this.paymentTransactionRepository.findById(id);
        PaymentTransactionDTO paymentTransactionDTO = null;

        if (paymentTransaction.isPresent()) {
            paymentTransactionDTO = this.paymentTransactionConverter.toDTO(paymentTransaction.get());
        }

        return paymentTransactionDTO;
    }

    @Override
    public List<PaymentTransactionDTO> getAllPaymentTransaction() {
        return this.paymentTransactionRepository.findAll().stream().map(paymentTransactionConverter::toDTO).toList();
    }

    @Override
    public List<PaymentTransactionDTO> getAllPaymentTransactionByVendorId(Long id) {
        return this.paymentTransactionRepository.findAllByVendorId(id).stream().map(paymentTransactionConverter::toDTO).toList();
    }

    @Override
    public PaymentTransactionDTO createPaymentTransaction(PaymentTransactionDTO paymentTransactionDTO) {
        return paymentTransactionConverter.toDTO(this.paymentTransactionRepository.save(this.paymentTransactionConverter.toEntity(paymentTransactionDTO)));
    }


    @Override
    public TransactionType getTransactionTypeById(Long id) {
        Optional<TransactionType> transactionType = this.transactionTypeRepository.findById(id);
        return transactionType.orElse(null);
    }

    @Override
    public PaymentTransactionDTO updatePaymentTransaction(Long id, PaymentTransactionDTO paymentTransactionDTO) {

        Optional<PaymentTransaction> existingPaymentTransaction = this.paymentTransactionRepository.findById(id);
        if (existingPaymentTransaction.isPresent()) {
            return this.paymentTransactionConverter.toDTO(this.paymentTransactionRepository.save(this.paymentTransactionConverter.toEntity(paymentTransactionDTO)));
        }
        return null;
    }

    @Override
    public void deletePaymentTransaction(Long id) {
        this.paymentTransactionRepository.deleteById(id);
    }
}
