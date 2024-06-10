package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.Invoice;
import com.pymntprocessing.pymntprocessing.model.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.model.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.repository.InvoiceRepository;
import com.pymntprocessing.pymntprocessing.repository.InvoiceStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    private final InvoiceRepository invoiceRepository;
    private final InvoiceStatusRepository invoiceStatusRepository;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository paymentTransactionRepository, InvoiceStatusRepository invoiceStatusRepository) {
        this.invoiceRepository = paymentTransactionRepository;
        this.invoiceStatusRepository = invoiceStatusRepository;
    }


    @Override
    public Invoice getInvoiceById(Long id) {
        Optional<Invoice> invoice = this.invoiceRepository.findById(id);
        return invoice.orElse(null);
    }

    @Override
    public List<Invoice> getAllInvoice() {
        return this.invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> getAllInvoiceByVendorId(Long id) {
        return this.invoiceRepository.findAllByVendorId(id);
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        return this.invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceStatus getInvoiceStatusById(Long id) {
        Optional<InvoiceStatus> invoiceStatus = this.invoiceStatusRepository.findById(id);
        return invoiceStatus.orElse(null);
    }


    @Override
    public Invoice updateInvoice(Long id, Invoice invoice) {

        Optional<Invoice> existingInvoice = this.invoiceRepository.findById(id);
        if (existingInvoice.isPresent()) {
            return this.invoiceRepository.save(invoice);
        }
        return null;
    }

    @Override
    public void deleteInvoice(Long id) {
        this.invoiceRepository.deleteById(id);
    }
}
