package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.constant.db.InvoiceStatusValue;
import com.pymntprocessing.pymntprocessing.constant.db.TransactionTypeValue;
import com.pymntprocessing.pymntprocessing.model.*;
import com.pymntprocessing.pymntprocessing.service.InvoiceService;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/invoice")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final VendorService vendorService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, VendorService vendorService) {
        this.invoiceService = invoiceService;
        this.vendorService = vendorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<Invoice>> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = this.invoiceService.getInvoiceById(id);

        if (invoice != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(invoice, true, ""));
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage<>(null, false, "Payment transaction not found!"));
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<List<Invoice>>> getAllInvoice() {
        List<Invoice> invoices = this.invoiceService.getAllInvoice();

        if (invoices.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<List<Invoice>>(invoices, false, "Payment transactions not found!"));

        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<List<Invoice>>(invoices, true, ""));

    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponseMessage<List<Invoice>>> getAllInvoiceByVendorId(@PathVariable("vendorId") Long id) {
        List<Invoice> invoices = this.invoiceService.getAllInvoiceByVendorId(id);

        if (invoices.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<List<Invoice>>(invoices, false, "Vendor doesn't have any payment transaction!"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<List<Invoice>>(invoices, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<Invoice>> createInvoice(@RequestBody Invoice invoice) {
        Vendor vendor = invoice.getVendor();
        InvoiceStatus invoiceStatus = invoice.getInvoiceStatus();

        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (vendor == null || vendor.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        }
        else if (invoiceStatus == null || invoiceStatus.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Invoice status not provided!";
        }

        if (invalidRequestBody) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        /**
         * check if it's a valid vendor
         */

        Vendor existingVendor = this.vendorService.getVendorById(vendor.getId());

        if (existingVendor == null) {
            invalidRequestBody = true;
            errorMessage = "Invalid vendor provided!";
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        /**
         * check if it's a valid transaction status
         */
        InvoiceStatus existingInvoiceStatus = this.invoiceService.getInvoiceStatusById(invoiceStatus.getId());
        if (existingInvoiceStatus == null || !Objects.equals(invoiceStatus.getName(), InvoiceStatusValue.OPEN.toString())) {
            invalidRequestBody = true;
            errorMessage = "Invalid transaction status provided!";
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }


        try {
            Invoice newInvoice = this.invoiceService.createInvoice(invoice);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage<>(newInvoice, true, "Payment transaction created!"));
        } catch (DataIntegrityViolationException e) {
            errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        } catch (Exception ex) {
            errorMessage = "ERROR: Internal Server Error! " + ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

    }

    @PutMapping("/{id")
    public ResponseEntity<ResponseMessage<Invoice>> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {

        Vendor vendor = invoice.getVendor();
        InvoiceStatus invoiceStatus = invoice.getInvoiceStatus();

        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (vendor == null || vendor.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        }
        else if (invoiceStatus == null || invoiceStatus.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction status not provided!";
        }

        if (invalidRequestBody) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        /**
         * check if it's a valid vendor
         */

        Vendor existingVendor = this.vendorService.getVendorById(vendor.getId());

        if (existingVendor == null) {
            invalidRequestBody = true;
            errorMessage = "Invalid vendor provided!";
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        /**
         * Check if valid transaction status and if transaction status is still open or committed
         */
        InvoiceStatus existingInvoiceStatus = this.invoiceService.getInvoiceStatusById(invoiceStatus.getId());
        if (existingInvoiceStatus == null || (
                !Objects.equals(invoiceStatus.getName(), InvoiceStatusValue.OPEN.toString())
                        && !Objects.equals(invoiceStatus.getName(), InvoiceStatusValue.COMMITTED.toString())
        )) {
            invalidRequestBody = true;
            errorMessage = "Unable to update transaction. Transaction status: " + invoiceStatus.getName();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }


        try {
            Invoice updatedInvoice = this.invoiceService.updateInvoice(id, invoice);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(updatedInvoice, true, "Invoice has been updated!"));
        } catch (DataIntegrityViolationException e) {
            errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        } catch (Exception ex) {
            errorMessage = "ERROR: Internal Server Error! " + ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage<Invoice>> deleteInvoice(@PathVariable Long id) {
        Invoice invoice = this.invoiceService.getInvoiceById(id);

        if (invoice == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, "Invalid id provided"));
        }

        InvoiceStatus invoiceStatus = invoice.getInvoiceStatus();
        /**
         * Check if valid transaction status and if transaction status is not paid
         */
        InvoiceStatus existingInvoiceStatus = this.invoiceService.getInvoiceStatusById(invoiceStatus.getId());
        if (existingInvoiceStatus == null || (
                Objects.equals(invoiceStatus.getName(), InvoiceStatusValue.PAID.toString())
        )) {
            String errorMessage = "Unable to delete transaction. Transaction status: " + invoiceStatus.getName();

            if (existingInvoiceStatus == null) {
                errorMessage = "Invalid transaction status provided!";
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        this.invoiceService.deleteInvoice(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<>(null, true, "Invoice " + invoice.getInvoiceNumber() + " has been deleted"));
    }
}
