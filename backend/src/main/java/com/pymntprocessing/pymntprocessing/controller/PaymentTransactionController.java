package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.ResponseMessage;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paymenttransaction")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentTransactionController {
    private final PaymentTransactionService paymentTransactionService;

    @Autowired
    public PaymentTransactionController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
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
}
