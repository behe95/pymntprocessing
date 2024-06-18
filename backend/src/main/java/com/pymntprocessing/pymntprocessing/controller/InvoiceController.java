package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.constant.db.InvoiceStatusValue;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.entity.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
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
@RequestMapping(ApiConstants.V1.Invoice.INVOICE_PATH)
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
    public ResponseEntity<ResponsePayload<InvoiceDTO>> getInvoiceById(@PathVariable Long id) {
        InvoiceDTO invoiceDTO = this.invoiceService.getInvoiceById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(invoiceDTO, true, ""));
    }

    @GetMapping
    public ResponseEntity<ResponsePayload<List<InvoiceDTO>>> getAllInvoice() {
        List<InvoiceDTO> invoiceDTOS = this.invoiceService.getAllInvoice();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<InvoiceDTO>>(invoiceDTOS, true, ""));

    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponsePayload<List<InvoiceDTO>>> getAllInvoiceByVendorId(@PathVariable("vendorId") Long id) {
        List<InvoiceDTO> invoiceDTOS = this.invoiceService.getAllInvoiceByVendorId(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<InvoiceDTO>>(invoiceDTOS, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<InvoiceDTO>> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        Vendor vendor = invoiceDTO.getVendor();
        InvoiceStatus invoiceStatus = invoiceDTO.getInvoiceStatus();

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
            throw new InvalidDataProvidedException(errorMessage);
        }


        InvoiceDTO newInvoiceDTO = this.invoiceService.createInvoice(invoiceDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponsePayload<>(newInvoiceDTO, true, "Invoice created!"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<InvoiceDTO>> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDTO) {

        Vendor vendor = invoiceDTO.getVendor();
        InvoiceStatus invoiceStatus = invoiceDTO.getInvoiceStatus();

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
            throw new InvalidDataProvidedException(errorMessage);
        }

        InvoiceDTO updatedInvoiceDTO = this.invoiceService.updateInvoice(id, invoiceDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(updatedInvoiceDTO, true, "Invoice has been updated!"));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ResponsePayload<InvoiceDTO>> deleteInvoice(@PathVariable Long id) {
        this.invoiceService.deleteInvoice(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(null, true, "Invoice has been deleted!"));
    }
}
