package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
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

    private final PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    public PaymentTransactionImpl(PaymentTransactionRepository paymentTransactionRepository, TransactionTypeRepository transactionTypeRepository, ModelMapper modelMapper, PaymentTransactionMapper paymentTransactionMapper) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.transactionTypeRepository = transactionTypeRepository;
        this.modelMapper = modelMapper;
        this.paymentTransactionMapper = paymentTransactionMapper;
    }

    @Override
    public PaymentTransactionDTO getPaymentTransactionById(Long id) {
        return this.paymentTransactionRepository.findById(id).map(this.paymentTransactionMapper::convertToDTO).orElse(null);
    }

    @Override
    public List<PaymentTransactionDTO> getAllPaymentTransaction() {
        return this.paymentTransactionRepository.findAll().stream().map(paymentTransactionMapper::convertToDTO).toList();
    }

    @Override
    public List<PaymentTransactionDTO> getAllPaymentTransactionByVendorId(Long id) {
        return this.paymentTransactionRepository.findAllByVendorId(id).stream().map(paymentTransactionMapper::convertToDTO).toList();
    }

    @Override
    public PaymentTransactionDTO createPaymentTransaction(PaymentTransactionDTO paymentTransactionDTO) {
        return paymentTransactionMapper.convertToDTO(this.paymentTransactionRepository.save(this.paymentTransactionMapper.convertToEntity(paymentTransactionDTO)));
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
            return this.paymentTransactionMapper.convertToDTO(this.paymentTransactionRepository.save(this.paymentTransactionMapper.convertToEntity(paymentTransactionDTO)));
        }
        return null;
    }

    @Override
    public void deletePaymentTransaction(Long id) {
        this.paymentTransactionRepository.deleteById(id);
    }
}
