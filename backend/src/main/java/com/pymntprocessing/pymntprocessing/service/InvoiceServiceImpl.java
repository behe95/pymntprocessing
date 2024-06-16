package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.mapper.InvoiceMapper;
import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Invoice;
import com.pymntprocessing.pymntprocessing.model.entity.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
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

    private final InvoiceMapper invoiceMapper;
    private final PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository paymentTransactionRepository, InvoiceStatusRepository invoiceStatusRepository, ModelMapper modelMapper, InvoiceMapper invoiceMapper, PaymentTransactionMapper paymentTransactionMapper) {
        this.invoiceRepository = paymentTransactionRepository;
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.modelMapper = modelMapper;
        this.invoiceMapper = invoiceMapper;
        this.paymentTransactionMapper = paymentTransactionMapper;
    }


    @Override
    public InvoiceDTO getInvoiceById(Long id) {
        Optional<Invoice> invoice = this.invoiceRepository.findById(id);
        InvoiceDTO invoiceDTO = null;

        if (invoice.isPresent()) {
            invoiceDTO = this.modelMapper.map(invoice.get(), InvoiceDTO.class);
            invoiceDTO.setPaymentTransactionDTO(this.paymentTransactionMapper.convertToDTOList(invoice.get().getPaymentTransaction()));

        }
        System.out.println(invoiceDTO);

        return invoiceDTO;
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
        return this.invoiceMapper.convertToDTO(this.invoiceRepository.save(this.invoiceMapper.convertToEntity(invoiceDTO)));
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
            return this.invoiceMapper.convertToDTO(this.invoiceRepository.save(this.invoiceMapper.convertToEntity(invoiceDTO)));
        }
        return null;
    }

    @Override
    public void deleteInvoice(Long id) {
        this.invoiceRepository.deleteById(id);
    }
}
