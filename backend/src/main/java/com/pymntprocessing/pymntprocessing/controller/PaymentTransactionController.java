package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.constant.db.TransactionTypeValue;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(paymentTransactionDTO, true, ""));
    }

    @GetMapping
    public ResponseEntity<ResponsePayload<List<PaymentTransactionDTO>>> getAllPaymentTransaction() {
        List<PaymentTransactionDTO> paymentTransactionDTOS = this.paymentTransactionService.getAllPaymentTransaction();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<PaymentTransactionDTO>>(paymentTransactionDTOS, true, ""));

    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponsePayload<List<PaymentTransactionDTO>>> getAllPaymentTransactionByVendorId(@PathVariable("vendorId") Long id) {
        List<PaymentTransactionDTO> paymentTransactionDTOS = this.paymentTransactionService.getAllPaymentTransactionByVendorId(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<PaymentTransactionDTO>>(paymentTransactionDTOS, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> createPaymentTransaction(@RequestBody PaymentTransactionDTO paymentTransactionDTO) {
        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (paymentTransactionDTO.getVendor() == null || paymentTransactionDTO.getVendor().getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        }
        else if (paymentTransactionDTO.getTransactionType() == null || paymentTransactionDTO.getTransactionType().getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction type not provided!";
        }

        if (invalidRequestBody) {
            throw new InvalidDataProvidedException(errorMessage);
        }


        PaymentTransactionDTO newPaymentTransactionDTO = this.paymentTransactionService.createPaymentTransaction(paymentTransactionDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponsePayload<>(newPaymentTransactionDTO, true, "Payment transaction created!"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> updatePaymentTransaction(@PathVariable Long id, @RequestBody PaymentTransactionDTO paymentTransactionDTO) {
        String errorMessage = "";
        boolean invalidRequestBody = false;

        /**
         * validate data
         */
        if (paymentTransactionDTO == null || !Objects.equals(paymentTransactionDTO.getId(), id)) {
            invalidRequestBody = true;
            errorMessage = "Invalid payment transaction provided!";
        } else if (paymentTransactionDTO.getVendor() == null || paymentTransactionDTO.getVendor().getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Vendor information not provided!";
        }
        else if (paymentTransactionDTO.getTransactionType() == null || paymentTransactionDTO.getTransactionType().getId() == null) {
            invalidRequestBody = true;
            errorMessage = "Transaction type not provided!";
        }

        if (invalidRequestBody) {
            throw new InvalidDataProvidedException(errorMessage);
        }

        PaymentTransactionDTO updatedPaymentTransactionDTO = this.paymentTransactionService.updatePaymentTransaction(id, paymentTransactionDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(updatedPaymentTransactionDTO, true, "Payment transaction updated!"));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> deletePaymentTransaction(@PathVariable Long id) {

        this.paymentTransactionService.deletePaymentTransaction(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(null, true, "Payment transaction has been deleted"));
    }
}
