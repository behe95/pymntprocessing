package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.entity.InvoiceStatus;

import java.util.List;

public interface InvoiceService {


    public InvoiceDTO getInvoiceById(Long id);
    public List<InvoiceDTO> getAllInvoice();

    public List<InvoiceDTO> getAllInvoiceByVendorId(Long id);

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO);

    public InvoiceStatus getInvoiceStatusById(Long id);


    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO);


    public void deleteInvoice(Long id);

}
