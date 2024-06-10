package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.*;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionControllerTest {

    @Mock
    private PaymentTransactionService paymentTransactionService;

    @InjectMocks
    private PaymentTransactionController paymentTransactionController;

    private PaymentTransaction paymentTransaction1;
    private PaymentTransaction paymentTransaction2;

    @BeforeEach
    void setUp() {
        TransactionStatus transactionStatus = new TransactionStatus(1L, "Open", 1);
        TransactionType transactionType = new TransactionType(2L, "Debit", 1, 2);

        Vendor vendor1 = new Vendor();
        vendor1.setId(1L);
        vendor1.setName("Vendor One");
        vendor1.setAddress("11 Long Ave, NYC");
        vendor1.setVendorId("EVID0001");

        paymentTransaction1 = new PaymentTransaction();
        paymentTransaction1.setId(1L);
        paymentTransaction1.setVendor(vendor1);
        paymentTransaction1.setTransactionType(transactionType);
        paymentTransaction1.setTransactionStatus(transactionStatus);
        paymentTransaction1.setTransactionNumber(1);
        paymentTransaction1.setTransactionDescription("Payment Transaction Description 1");
        paymentTransaction1.setTransactionAmount(50.50);


        paymentTransaction2 = new PaymentTransaction();
        paymentTransaction2.setId(2L);
        paymentTransaction2.setVendor(vendor1);
        paymentTransaction2.setTransactionType(transactionType);
        paymentTransaction2.setTransactionStatus(transactionStatus);
        paymentTransaction2.setTransactionNumber(2);
        paymentTransaction2.setTransactionDescription("Payment Transaction Description 2");
        paymentTransaction2.setTransactionAmount(150.00);
    }

    @Test
    void getAllPaymentTransactionWhenExist() {
        // given
        given(this.paymentTransactionService.getAllPaymentTransaction()).willReturn(List.of(paymentTransaction1, paymentTransaction2));

        // when
        ResponseEntity<ResponseMessage<List<PaymentTransaction>>> responseEntity = this.paymentTransactionController.getAllPaymentTransaction();

        // then
        ResponseMessage<List<PaymentTransaction>> responseMessage = (ResponseMessage<List<PaymentTransaction>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransaction> paymentTransactions = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(2, paymentTransactions.size());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransaction();
    }

    @Test
    void getAllPaymentTransactionWhenNotExist() {
        // given
        given(this.paymentTransactionService.getAllPaymentTransaction()).willReturn(List.of());

        // when
        ResponseEntity<ResponseMessage<List<PaymentTransaction>>> responseEntity = this.paymentTransactionController.getAllPaymentTransaction();

        // then
        ResponseMessage<List<PaymentTransaction>> responseMessage = (ResponseMessage<List<PaymentTransaction>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransaction> paymentTransactions = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, paymentTransactions.size());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransaction();
    }

    @Test
    void getPaymentTransactionByIdWhenFound() {
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(1L)).willReturn(paymentTransaction1);

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.getPaymentTransactionById(1L);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction responsePaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(paymentTransaction1, responsePaymentTransaction);
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(1L);
    }

    @Test
    void getPaymentTransactionByIdWhenNotFound() {
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(11L)).willReturn(null);

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.getPaymentTransactionById(11L);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert  responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction responsePaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responsePaymentTransaction);
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(11L);

    }
}