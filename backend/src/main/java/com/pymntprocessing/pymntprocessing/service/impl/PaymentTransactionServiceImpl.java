package com.pymntprocessing.pymntprocessing.service.impl;

import com.pymntprocessing.pymntprocessing.constant.db.TransactionTypeValue;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.exception.InvoiceNotFoundException;
import com.pymntprocessing.pymntprocessing.exception.PaymentTransactionNotFoundException;
import com.pymntprocessing.pymntprocessing.exception.VendorNotFoundException;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.repository.PaymentTransactionRepository;
import com.pymntprocessing.pymntprocessing.repository.TransactionTypeRepository;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final VendorService vendorService;

    private final ModelMapper modelMapper;

    private final PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    public PaymentTransactionServiceImpl(PaymentTransactionRepository paymentTransactionRepository, TransactionTypeRepository transactionTypeRepository, VendorService vendorService, ModelMapper modelMapper, PaymentTransactionMapper paymentTransactionMapper) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.transactionTypeRepository = transactionTypeRepository;
        this.vendorService = vendorService;
        this.modelMapper = modelMapper;
        this.paymentTransactionMapper = paymentTransactionMapper;
    }

    @Override
    public PaymentTransactionDTO getPaymentTransactionById(Long id) {
        return this.paymentTransactionRepository.findById(id).map(this.paymentTransactionMapper::convertToDTO).orElseThrow(PaymentTransactionNotFoundException::new);
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
        String errorMessage = "";

        /**
         * check if it's a valid vendor
         */

        Vendor existingVendor = this.vendorService.getVendorById(paymentTransactionDTO.getVendor().getId());

        if (existingVendor == null) {
            errorMessage = "Invalid vendor provided!";
            throw new InvalidDataProvidedException(errorMessage);
        }


        /**
         * check if it's a valid transaction type
         */
        TransactionType transactionType = paymentTransactionDTO.getTransactionType();
        TransactionType existingTransactionType = this.getTransactionTypeById(paymentTransactionDTO.getTransactionType().getId());
        if (existingTransactionType == null
                || (!Objects.equals(transactionType.getName(), TransactionTypeValue.DEBIT.toString())
                && !Objects.equals(transactionType.getName(), TransactionTypeValue.CREDIT.toString()))) {
            errorMessage = "Invalid transaction type provided!";
            throw new InvalidDataProvidedException(errorMessage);
        }

        return paymentTransactionMapper.convertToDTO(this.paymentTransactionRepository.save(this.paymentTransactionMapper.convertToEntity(paymentTransactionDTO)));
    }


    @Override
    public TransactionType getTransactionTypeById(Long id) {
        return this.transactionTypeRepository.findById(id).orElse(null);
    }

    @Override
    public PaymentTransactionDTO updatePaymentTransaction(Long id, PaymentTransactionDTO paymentTransactionDTO) {

        String errorMessage = "";

        /**
         * check if it's a valid vendor
         */



        try {
            Vendor existingVendor = this.vendorService.getVendorById(paymentTransactionDTO.getVendor().getId());
        } catch (VendorNotFoundException ex) {
            errorMessage = "Invalid vendor provided!";
            throw new InvalidDataProvidedException(errorMessage);
        }


        /**
         * check if it's a valid transaction type
         */
        TransactionType transactionType = paymentTransactionDTO.getTransactionType();
        TransactionType existingTransactionType = this.getTransactionTypeById(paymentTransactionDTO.getTransactionType().getId());
        if (existingTransactionType == null
                || (!Objects.equals(transactionType.getName(), TransactionTypeValue.DEBIT.toString())
                && !Objects.equals(transactionType.getName(), TransactionTypeValue.CREDIT.toString()))) {
            errorMessage = "Invalid transaction type provided!";
            throw new InvalidDataProvidedException(errorMessage);
        }

        Optional<PaymentTransaction> existingPaymentTransaction = this.paymentTransactionRepository.findById(id);
        if (existingPaymentTransaction.isPresent()) {
            return this.paymentTransactionMapper.convertToDTO(this.paymentTransactionRepository.save(this.paymentTransactionMapper.convertToEntity(paymentTransactionDTO)));
        } else {
            throw new InvoiceNotFoundException();
        }
    }

    @Override
    public void deletePaymentTransaction(Long id) {

        try {
            PaymentTransactionDTO paymentTransactionDTO = this.getPaymentTransactionById(id);
        }
        catch (PaymentTransactionNotFoundException ex) {
            throw new InvalidDataProvidedException("Invalid id provided!");
        }
        this.paymentTransactionRepository.deleteById(id);
    }

    @Override
    public List<PaymentTransactionDTO> getAllPaymentTransactionsByIds(List<Long> ids) {
        return this.paymentTransactionMapper.convertToDTOList(paymentTransactionRepository.findAllByIdIn(ids));
    }
}
