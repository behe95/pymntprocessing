package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.dto.InvoiceConverter;
import com.pymntprocessing.pymntprocessing.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.entity.Invoice;
import com.pymntprocessing.pymntprocessing.entity.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.repository.InvoiceRepository;
import com.pymntprocessing.pymntprocessing.repository.InvoiceStatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    private final InvoiceRepository invoiceRepository;
    private final InvoiceStatusRepository invoiceStatusRepository;

    private final ModelMapper modelMapper;

    private final InvoiceConverter invoiceConverter;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository paymentTransactionRepository, InvoiceStatusRepository invoiceStatusRepository, ModelMapper modelMapper, InvoiceConverter invoiceConverter) {
        this.invoiceRepository = paymentTransactionRepository;
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.modelMapper = modelMapper;
        this.invoiceConverter = invoiceConverter;
    }


    @Override
    public InvoiceDTO getInvoiceById(Long id) {
        Optional<Invoice> invoice = this.invoiceRepository.findById(id);
        InvoiceDTO invoiceDTO = null;

        if (invoice.isPresent()) {
            invoiceDTO = this.modelMapper.map(invoice.get(), InvoiceDTO.class);
            List<PaymentTransactionDTO> paymentTransactionDTOS = invoice.get().getPaymentTransaction().stream().map(paymentTransaction -> this.modelMapper.map(paymentTransaction, PaymentTransactionDTO.class)).toList();
            invoiceDTO.setPaymentTransactionDTO(paymentTransactionDTOS);
        }
        System.out.println(invoiceDTO);

        return invoiceDTO;
    }

    @Override
    public List<InvoiceDTO> getAllInvoice() {
        return this.invoiceRepository.findAll().stream().map(this.invoiceConverter::toDTO).toList();
    }

    @Override
    public List<InvoiceDTO> getAllInvoiceByVendorId(Long id) {
        return this.invoiceRepository.findAllByVendorId(id).stream().map(this.invoiceConverter::toDTO).toList();
    }

    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        return this.invoiceConverter.toDTO(this.invoiceRepository.save(this.invoiceConverter.toEntity(invoiceDTO)));
    }

    @Override
    public InvoiceStatus getInvoiceStatusById(Long id) {
        Optional<InvoiceStatus> invoiceStatus = this.invoiceStatusRepository.findById(id);
        return invoiceStatus.orElse(null);
    }


    @Override
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {

        Optional<Invoice> existingInvoice = this.invoiceRepository.findById(id);
        if (existingInvoice.isPresent()) {
            return this.invoiceConverter.toDTO(this.invoiceRepository.save(this.invoiceConverter.toEntity(invoiceDTO)));
        }
        return null;
    }

    @Override
    public void deleteInvoice(Long id) {
        this.invoiceRepository.deleteById(id);
    }
}
