package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.constant.db.TransactionTypeValue;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(ApiConstants.V1.PaymentTransaction.PAYMENT_TRANSACTION_PATH)
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentTransactionController {
    private final PaymentTransactionService paymentTransactionService;
    private final VendorService vendorService;

    @Autowired
    public PaymentTransactionController(PaymentTransactionService paymentTransactionService, VendorService vendorService) {
        this.paymentTransactionService = paymentTransactionService;
        this.vendorService = vendorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> getPaymentTransactionById(@PathVariable Long id) {
        PaymentTransactionDTO paymentTransactionDTO = this.paymentTransactionService.getPaymentTransactionById(id);

        if (paymentTransactionDTO != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponsePayload<>(paymentTransactionDTO, true, ""));
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponsePayload<>(null, false, "Payment transaction not found!"));
    }

    @GetMapping
    public ResponseEntity<ResponsePayload<List<PaymentTransactionDTO>>> getAllPaymentTransaction() {
        List<PaymentTransactionDTO> paymentTransactionDTOS = this.paymentTransactionService.getAllPaymentTransaction();

        if (paymentTransactionDTOS.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponsePayload<List<PaymentTransactionDTO>>(paymentTransactionDTOS, false, "Payment transactions not found!"));

        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<PaymentTransactionDTO>>(paymentTransactionDTOS, true, ""));

    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponsePayload<List<PaymentTransactionDTO>>> getAllPaymentTransactionByVendorId(@PathVariable("vendorId") Long id) {
        List<PaymentTransactionDTO> paymentTransactionDTOS = this.paymentTransactionService.getAllPaymentTransactionByVendorId(id);

        if (paymentTransactionDTOS.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponsePayload<List<PaymentTransactionDTO>>(paymentTransactionDTOS, false, "Vendor doesn't have any payment transaction!"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<PaymentTransactionDTO>>(paymentTransactionDTOS, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> createPaymentTransaction(@RequestBody PaymentTransactionDTO paymentTransactionDTO) {
        Vendor vendor = paymentTransactionDTO.getVendor();
//        TransactionStatus transactionStatus = paymentTransaction.getTransactionStatus();
        TransactionType transactionType = paymentTransactionDTO.getTransactionType();

        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (vendor == null || vendor.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        }
//        else if (transactionStatus == null || transactionStatus.getId() == null) {
//            invalidRequestBody = true;
//            errorMessage = "Transaction status not provided!";
//        }
        else if (transactionType == null || transactionType.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction type not provided!";
        }

        if (invalidRequestBody) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, errorMessage));
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
                    .body(new ResponsePayload<>(null, false, errorMessage));
        }

//        /**
//         * check if it's a valid transaction status
//         */
//        TransactionStatus existingTransactionStatus = this.paymentTransactionService.getTransactionStatusById(transactionStatus.getId());
//        if (existingTransactionStatus == null || !Objects.equals(transactionStatus.getName(), InvoiceStatusValue.OPEN.toString())) {
//            invalidRequestBody = true;
//            errorMessage = "Invalid transaction status provided!";
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(new ResponsePayload<>(null, false, errorMessage));
//        }

        /**
         * check if it's a valid transaction type
         */
        TransactionType existingTransactionType = this.paymentTransactionService.getTransactionTypeById(transactionType.getId());
        if (existingTransactionType == null
                || (!Objects.equals(transactionType.getName(), TransactionTypeValue.DEBIT.toString())
                && !Objects.equals(transactionType.getName(), TransactionTypeValue.CREDIT.toString()))) {
            invalidRequestBody = true;
            errorMessage = "Invalid transaction type provided!";
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        }

        try {
            PaymentTransactionDTO newPaymentTransactionDTO = this.paymentTransactionService.createPaymentTransaction(paymentTransactionDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponsePayload<>(newPaymentTransactionDTO, true, "Payment transaction created!"));
        } catch (DataIntegrityViolationException e) {
            errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        } catch (Exception ex) {
            errorMessage = "ERROR: Internal Server Error! " + ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> updatePaymentTransaction(@PathVariable Long id, @RequestBody PaymentTransactionDTO paymentTransactionDTO) {

        Vendor vendor = paymentTransactionDTO.getVendor();
//        TransactionStatus transactionStatus = paymentTransaction.getTransactionStatus();
        TransactionType transactionType = paymentTransactionDTO.getTransactionType();

        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (vendor == null || vendor.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        }
//        else if (transactionStatus == null || transactionStatus.getId() == null) {
//            invalidRequestBody = true;
//            errorMessage = "Transaction status not provided!";
//        }
        else if (transactionType == null || transactionType.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction type not provided!";
        }

        if (invalidRequestBody) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, errorMessage));
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
                    .body(new ResponsePayload<>(null, false, errorMessage));
        }

//        /**
//         * Check if valid transaction status and if transaction status is still open or committed
//         */
//        TransactionStatus existingTransactionStatus = this.paymentTransactionService.getTransactionStatusById(transactionStatus.getId());
//        if (existingTransactionStatus == null || (
//                !Objects.equals(transactionStatus.getName(), InvoiceStatusValue.OPEN.toString())
//                        && !Objects.equals(transactionStatus.getName(), InvoiceStatusValue.COMMITTED.toString())
//        )) {
//            invalidRequestBody = true;
//            errorMessage = "Unable to update transaction. Transaction status: " + transactionStatus.getName();
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(new ResponsePayload<>(null, false, errorMessage));
//        }

        /**
         * check if it's a valid transaction type
         */
        TransactionType existingTransactionType = this.paymentTransactionService.getTransactionTypeById(transactionType.getId());
        if (existingTransactionType == null
                || (!Objects.equals(transactionType.getName(), TransactionTypeValue.DEBIT.toString())
                && !Objects.equals(transactionType.getName(), TransactionTypeValue.CREDIT.toString()))) {
            invalidRequestBody = true;
            errorMessage = "Invalid transaction type provided!";
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        }

        try {
            PaymentTransactionDTO updatedPaymentTransactionDTO = this.paymentTransactionService.updatePaymentTransaction(id, paymentTransactionDTO);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponsePayload<>(updatedPaymentTransactionDTO, true, "Payment transaction has been updated!"));
        } catch (DataIntegrityViolationException e) {
            errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        } catch (Exception ex) {
            errorMessage = "ERROR: Internal Server Error! " + ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> deletePaymentTransaction(@PathVariable Long id) {
        PaymentTransactionDTO paymentTransactionDTO = this.paymentTransactionService.getPaymentTransactionById(id);

        if (paymentTransactionDTO == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, "Invalid id provided"));
        }

//        TransactionStatus transactionStatus = paymentTransaction.getTransactionStatus();
//        /**
//         * Check if valid transaction status and if transaction status is not paid
//         */
//        TransactionStatus existingTransactionStatus = this.paymentTransactionService.getTransactionStatusById(transactionStatus.getId());
//        if (existingTransactionStatus == null || (
//                Objects.equals(transactionStatus.getName(), InvoiceStatusValue.PAID.toString())
//        )) {
//            String errorMessage = "Unable to delete transaction. Transaction status: " + transactionStatus.getName();
//
//            if (existingTransactionStatus == null) {
//                errorMessage = "Invalid transaction status provided!";
//            }
//
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(new ResponsePayload<>(null, false, errorMessage));
//        }

        this.paymentTransactionService.deletePaymentTransaction(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(null, true, "Transaction " + paymentTransactionDTO.getTransactionNumber() + " has been deleted"));
    }
}
