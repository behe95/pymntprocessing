package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.model.Invoice;
import com.pymntprocessing.pymntprocessing.model.TransactionType;

import java.util.List;

public interface InvoiceService {


    public Invoice getInvoiceById(Long id);
    public List<Invoice> getAllInvoice();

    public List<Invoice> getAllInvoiceByVendorId(Long id);

    public Invoice createInvoice(Invoice invoice);

    public InvoiceStatus getInvoiceStatusById(Long id);


    public Invoice updateInvoice(Long id, Invoice invoice);


    public void deleteInvoice(Long id);

}
