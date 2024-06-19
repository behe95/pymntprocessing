package com.pymntprocessing.pymntprocessing.service.impl;

import com.pymntprocessing.pymntprocessing.constant.db.InvoiceStatusValue;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.exception.InvoiceNotFoundException;
import com.pymntprocessing.pymntprocessing.exception.PaymentTransactionNotFoundException;
import com.pymntprocessing.pymntprocessing.exception.VendorNotFoundException;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.model.mapper.InvoiceMapper;
import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Invoice;
import com.pymntprocessing.pymntprocessing.model.entity.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
import com.pymntprocessing.pymntprocessing.repository.InvoiceRepository;
import com.pymntprocessing.pymntprocessing.repository.InvoiceStatusRepository;
import com.pymntprocessing.pymntprocessing.service.InvoiceService;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceStatusRepository invoiceStatusRepository;

    private final VendorService vendorService;

    private final ModelMapper modelMapper;

    private final InvoiceMapper invoiceMapper;
    private final PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository paymentTransactionRepository, InvoiceStatusRepository invoiceStatusRepository, VendorService vendorService, ModelMapper modelMapper, InvoiceMapper invoiceMapper, PaymentTransactionMapper paymentTransactionMapper) {
        this.invoiceRepository = paymentTransactionRepository;
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.vendorService = vendorService;
        this.modelMapper = modelMapper;
        this.invoiceMapper = invoiceMapper;
        this.paymentTransactionMapper = paymentTransactionMapper;
    }


    @Override
    public InvoiceDTO getInvoiceById(Long id) {
        return this.invoiceRepository.findById(id).map(this.invoiceMapper::convertToDTO).orElseThrow(InvoiceNotFoundException::new);
    }

    @Override
    public List<InvoiceDTO> getAllInvoice() {
        return this.invoiceRepository.findAll().stream().map(this.invoiceMapper::convertToDTO).toList();
    }

    @Override
    public List<InvoiceDTO> getAllInvoiceByVendorId(Long id) {
        return this.invoiceRepository.findAllByVendorId(id).stream().map(this.invoiceMapper::convertToDTO).toList();
    }

    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        String errorMessage = "";

        /**
         * check if it's a valid vendor
         */

        try {
            Vendor existingVendor = this.vendorService.getVendorById(invoiceDTO.getVendor().getId());
        } catch (VendorNotFoundException e) {
            errorMessage = "Invalid vendor provided! " + e.getMessage();
            throw new InvalidDataProvidedException(errorMessage);
        }

        /**
         * check if it's a valid transaction status
         */
        InvoiceStatus existingInvoiceStatus = this.getInvoiceStatusById(invoiceDTO.getInvoiceStatus().getId());
        if (existingInvoiceStatus == null || !Objects.equals(invoiceDTO.getInvoiceStatus().getName(), InvoiceStatusValue.OPEN.toString())) {
            errorMessage = "Invalid invoice status provided!";
            throw new InvalidDataProvidedException(errorMessage);
        }
        return this.invoiceMapper.convertToDTO(this.invoiceRepository.save(this.invoiceMapper.convertToEntity(invoiceDTO)));
    }

    @Override
    public InvoiceStatus getInvoiceStatusById(Long id) {
        Optional<InvoiceStatus> invoiceStatus = this.invoiceStatusRepository.findById(id);
        return invoiceStatus.orElse(null);
    }


    @Override
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        String errorMessage = "";

        Invoice existingInvoice = this.invoiceRepository.findById(id).orElseThrow(InvoiceNotFoundException::new);

        /**
         * check if it's a valid vendor
         */

        try {
            Vendor existingVendor = this.vendorService.getVendorById(invoiceDTO.getVendor().getId());
        } catch (VendorNotFoundException e) {
            errorMessage = "Invalid vendor provided! " + e.getMessage();
            throw new InvalidDataProvidedException(errorMessage);
        }

        /**
         * Check if valid transaction status and if transaction status is still open or committed
         */
        InvoiceStatus existingInvoiceStatus = this.getInvoiceStatusById(existingInvoice.getInvoiceStatus().getId());

        if (existingInvoiceStatus == null) {
            errorMessage = "Invalid transaction status provided!";
            throw new InvalidDataProvidedException(errorMessage);
        }

        if (!Objects.equals(invoiceDTO.getInvoiceStatus().getName(), InvoiceStatusValue.OPEN.toString()) && !Objects.equals(invoiceDTO.getInvoiceStatus().getName(), InvoiceStatusValue.COMMITTED.toString())) {
            errorMessage = "Unable to update transaction. Transaction status: " + invoiceDTO.getInvoiceStatus().getName();
            throw new InvalidDataProvidedException(errorMessage);
        }

        return this.invoiceMapper.convertToDTO(this.invoiceRepository.save(this.invoiceMapper.convertToEntity(invoiceDTO)));
    }

    @Override
    public void deleteInvoice(Long id) {

        String errorMessage = "";
        InvoiceDTO invoiceDTO = this.getInvoiceById(id);

        InvoiceStatus invoiceStatus = invoiceDTO.getInvoiceStatus();

        if (Objects.equals(invoiceStatus.getName(), InvoiceStatusValue.PAID.toString())) {
            errorMessage = "Unable to delete transaction. Transaction status: " + invoiceStatus.getName();
            throw new InvalidDataProvidedException(errorMessage);
        }
        this.invoiceRepository.deleteById(id);
    }
}
