package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.constant.db.TransactionStatusValue;
import com.pymntprocessing.pymntprocessing.constant.db.TransactionTypeValue;
import com.pymntprocessing.pymntprocessing.model.*;
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
@RequestMapping("/paymenttransaction")
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
    public ResponseEntity<ResponseMessage<PaymentTransaction>> getPaymentTransactionById(@PathVariable Long id) {
        PaymentTransaction paymentTransaction = this.paymentTransactionService.getPaymentTransactionById(id);

        if (paymentTransaction != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(paymentTransaction, true, ""));
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage<>(null, false, "Payment transaction not found!"));
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<List<PaymentTransaction>>> getAllPaymentTransaction() {
        List<PaymentTransaction> paymentTransactions = this.paymentTransactionService.getAllPaymentTransaction();

        if (paymentTransactions.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<List<PaymentTransaction>>(paymentTransactions, false, "Payment transactions not found!"));

        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<List<PaymentTransaction>>(paymentTransactions, true, ""));

    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponseMessage<List<PaymentTransaction>>> getAllPaymentTransactionByVendorId(@PathVariable("vendorId") Long id) {
        List<PaymentTransaction> paymentTransactions = this.paymentTransactionService.getAllPaymentTransactionByVendorId(id);

        if (paymentTransactions.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<List<PaymentTransaction>>(paymentTransactions, false, "Vendor doesn't have any payment transaction!"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<List<PaymentTransaction>>(paymentTransactions, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<PaymentTransaction>> createPaymentTransaction(@RequestBody PaymentTransaction paymentTransaction) {
        Vendor vendor = paymentTransaction.getVendor();
        TransactionStatus transactionStatus = paymentTransaction.getTransactionStatus();
        TransactionType transactionType = paymentTransaction.getTransactionType();

        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (vendor == null || vendor.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        } else if (transactionStatus == null || transactionStatus.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction status not provided!";
        } else if (transactionType == null || transactionType.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction type not provided!";
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
        TransactionStatus existingTransactionStatus = this.paymentTransactionService.getTransactionStatusById(transactionStatus.getId());
        if (existingTransactionStatus == null || !Objects.equals(transactionStatus.getName(), TransactionStatusValue.OPEN.toString())) {
            invalidRequestBody = true;
            errorMessage = "Invalid transaction status provided!";
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

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
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        try {
            PaymentTransaction newPaymentTransaction = this.paymentTransactionService.createPaymentTransaction(paymentTransaction);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage<>(newPaymentTransaction, true, "Payment transaction created!"));
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
    public ResponseEntity<ResponseMessage<PaymentTransaction>> updatePaymentTransaction(@PathVariable Long id, @RequestBody PaymentTransaction paymentTransaction) {

        Vendor vendor = paymentTransaction.getVendor();
        TransactionStatus transactionStatus = paymentTransaction.getTransactionStatus();
        TransactionType transactionType = paymentTransaction.getTransactionType();

        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (vendor == null || vendor.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        } else if (transactionStatus == null || transactionStatus.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction status not provided!";
        } else if (transactionType == null || transactionType.getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction type not provided!";
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
        TransactionStatus existingTransactionStatus = this.paymentTransactionService.getTransactionStatusById(transactionStatus.getId());
        if (existingTransactionStatus == null || (
                !Objects.equals(transactionStatus.getName(), TransactionStatusValue.OPEN.toString())
                        && !Objects.equals(transactionStatus.getName(), TransactionStatusValue.COMMITTED.toString())
        )) {
            invalidRequestBody = true;
            errorMessage = "Unable to update transaction. Transaction status: " + transactionStatus.getName();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

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
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        try {
            PaymentTransaction updatedPaymentTransaction = this.paymentTransactionService.updatePaymentTransaction(id, paymentTransaction);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(updatedPaymentTransaction, true, "Payment transaction has been updated!"));
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
    public ResponseEntity<ResponseMessage<PaymentTransaction>> deletePaymentTransaction(@PathVariable Long id) {
        PaymentTransaction paymentTransaction = this.paymentTransactionService.getPaymentTransactionById(id);

        if (paymentTransaction == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, "Invalid id provided"));
        }

        TransactionStatus transactionStatus = paymentTransaction.getTransactionStatus();
        /**
         * Check if valid transaction status and if transaction status is not paid
         */
        TransactionStatus existingTransactionStatus = this.paymentTransactionService.getTransactionStatusById(transactionStatus.getId());
        if (existingTransactionStatus == null || (
                Objects.equals(transactionStatus.getName(), TransactionStatusValue.PAID.toString())
        )) {
            String errorMessage = "Unable to delete transaction. Transaction status: " + transactionStatus.getName();

            if (existingTransactionStatus == null) {
                errorMessage = "Invalid transaction status provided!";
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

        this.paymentTransactionService.deletePaymentTransaction(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<>(null, true, "Transaction " + paymentTransaction.getTransactionNumber() + " has been deleted"));
    }
}
