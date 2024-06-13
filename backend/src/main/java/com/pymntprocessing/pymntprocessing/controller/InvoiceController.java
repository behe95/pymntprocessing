package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.constant.db.InvoiceStatusValue;
import com.pymntprocessing.pymntprocessing.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.entity.*;
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
    public ResponseEntity<ResponseMessage<InvoiceDTO>> getInvoiceById(@PathVariable Long id) {
        InvoiceDTO invoiceDTO = this.invoiceService.getInvoiceById(id);

        if (invoiceDTO != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(invoiceDTO, true, ""));
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage<>(null, false, "Payment transaction not found!"));
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<List<InvoiceDTO>>> getAllInvoice() {
        List<InvoiceDTO> invoiceDTOS = this.invoiceService.getAllInvoice();

        if (invoiceDTOS.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<List<InvoiceDTO>>(invoiceDTOS, false, "Payment transactions not found!"));

        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<List<InvoiceDTO>>(invoiceDTOS, true, ""));

    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponseMessage<List<InvoiceDTO>>> getAllInvoiceByVendorId(@PathVariable("vendorId") Long id) {
        List<InvoiceDTO> invoiceDTOS = this.invoiceService.getAllInvoiceByVendorId(id);

        if (invoiceDTOS.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<List<InvoiceDTO>>(invoiceDTOS, false, "Vendor doesn't have any payment transaction!"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<List<InvoiceDTO>>(invoiceDTOS, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<InvoiceDTO>> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
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
            errorMessage = "Invalid invoice status provided!";
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }


        try {
            InvoiceDTO newInvoiceDTO = this.invoiceService.createInvoice(invoiceDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage<>(newInvoiceDTO, true, "Payment transaction created!"));
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

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage<InvoiceDTO>> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDTO) {

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
            InvoiceDTO updatedInvoiceDTO = this.invoiceService.updateInvoice(id, invoiceDTO);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(updatedInvoiceDTO, true, "Invoice has been updated!"));
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
    public ResponseEntity<ResponseMessage<InvoiceDTO>> deleteInvoice(@PathVariable Long id) {
        InvoiceDTO invoiceDTO = this.invoiceService.getInvoiceById(id);

        if (invoiceDTO == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, "Invalid id provided"));
        }

        InvoiceStatus invoiceStatus = invoiceDTO.getInvoiceStatus();
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
                .body(new ResponseMessage<>(null, true, "Invoice " + invoiceDTO.getInvoiceNumber() + " has been deleted"));
    }
}
