package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.*;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionControllerTest {

    @Mock
    private PaymentTransactionService paymentTransactionService;

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private PaymentTransactionController paymentTransactionController;

    private PaymentTransaction paymentTransaction1;
    private PaymentTransaction paymentTransaction2;
    TransactionStatus transactionStatus;
    TransactionType transactionType;

    Vendor vendor1;

    @BeforeEach
    void setUp() {
        transactionStatus = new TransactionStatus(1L, "Open", 1);
        transactionType = new TransactionType(2L, "Debit", 1, 2);

        vendor1 = new Vendor();
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

    @Test
    void getAllPaymentTransactionByVendorIdWhenExist() {
        // given
        given(this.paymentTransactionService.getAllPaymentTransactionByVendorId(1L)).willReturn(List.of(paymentTransaction1, paymentTransaction2));

        // when
        ResponseEntity<ResponseMessage<List<PaymentTransaction>>> responseEntity = this.paymentTransactionController.getAllPaymentTransactionByVendorId(1L);

        // then
        ResponseMessage<List<PaymentTransaction>> responseMessage = (ResponseMessage<List<PaymentTransaction>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransaction> responsePaymentTransactions = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(2, responsePaymentTransactions.size());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransactionByVendorId(1L);
    }



    @Test
    void getAllPaymentTransactionByVendorIdWhenNotExist() {
        // given
        given(this.paymentTransactionService.getAllPaymentTransactionByVendorId(1L)).willReturn(List.of());

        // when
        ResponseEntity<ResponseMessage<List<PaymentTransaction>>> responseEntity = this.paymentTransactionController.getAllPaymentTransactionByVendorId(1L);

        // then
        ResponseMessage<List<PaymentTransaction>> responseMessage = (ResponseMessage<List<PaymentTransaction>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransaction> responsePaymentTransactions = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, responsePaymentTransactions.size());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransactionByVendorId(1L);
    }

    @Test
    void createPaymentTransactionWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity;
        ResponseMessage<PaymentTransaction> responseMessage;
        /**
         * Vendor not provided
         */
        PaymentTransaction newPaymentTransaction = paymentTransaction1;
        newPaymentTransaction.setVendor(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransaction.class));

        /**
         * Transaction status not provided
         */
        newPaymentTransaction.setVendor(vendor1);
        newPaymentTransaction.setTransactionStatus(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransaction.class));

        /**
         * Transaction type not provided
         */
        newPaymentTransaction.setVendor(vendor1);
        newPaymentTransaction.setTransactionStatus(transactionStatus);
        newPaymentTransaction.setTransactionType(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransaction.class));
    }

    @Test
    void createPaymentTransactionWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity;
        ResponseMessage<PaymentTransaction> responseMessage;
        /**
         * Invalid vendor provided
         */
        PaymentTransaction newPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransaction.class));

        reset(vendorService);
        /**
         * Invalid Transaction status provided
         */

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransaction.class));


        reset(vendorService);
        reset(paymentTransactionService);

        /**
         * Invalid Transaction type provided
         */

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransaction.class));
    }




    @Test
    void createPaymentTransaction() {

        PaymentTransaction newPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(newPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.createPaymentTransaction(newPaymentTransaction)).willReturn(newPaymentTransaction);

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction createdPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(newPaymentTransaction, createdPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(newPaymentTransaction.getVendor().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(newPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(newPaymentTransaction);

    }
    @Test
    void createVendorDuplicate() {

        PaymentTransaction newPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(newPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.createPaymentTransaction(newPaymentTransaction)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction createdPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(newPaymentTransaction.getVendor().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(newPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(newPaymentTransaction);

    }

    @Test
    void createVendorInternalServerErr() {

        PaymentTransaction newPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(newPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.createPaymentTransaction(newPaymentTransaction)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction createdPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(newPaymentTransaction.getVendor().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(newPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(newPaymentTransaction);

    }




    @Test
    void updatePaymentTransactionWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity;
        ResponseMessage<PaymentTransaction> responseMessage;
        /**
         * Vendor not provided
         */
        PaymentTransaction updatedPaymentTransaction = paymentTransaction1;
        updatedPaymentTransaction.setVendor(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class),any(PaymentTransaction.class));

        /**
         * Transaction status not provided
         */
        updatedPaymentTransaction.setVendor(vendor1);
        updatedPaymentTransaction.setTransactionStatus(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class),any(PaymentTransaction.class));

        /**
         * Transaction type not provided
         */
        updatedPaymentTransaction.setVendor(vendor1);
        updatedPaymentTransaction.setTransactionStatus(transactionStatus);
        updatedPaymentTransaction.setTransactionType(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransaction.class));
    }

    @Test
    void updatePaymentTransactionWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity;
        ResponseMessage<PaymentTransaction> responseMessage;
        /**
         * Invalid vendor provided
         */
        PaymentTransaction updatedPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransaction.class));

        reset(vendorService);
        /**
         * Invalid Transaction status provided
         */

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransaction.class));


        reset(vendorService);
        reset(paymentTransactionService);

        /**
         * Invalid Transaction type provided
         */

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransaction.class));
    }




    @Test
    void updatePaymentTransaction() {

        PaymentTransaction updatedPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(updatedPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction)).willReturn(updatedPaymentTransaction);

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction responseUpdatedPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(updatedPaymentTransaction, responseUpdatedPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(updatedPaymentTransaction.getVendor().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

    }
    @Test
    void updatedVendorDuplicate() {

        PaymentTransaction updatedPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(updatedPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction responseUpdatedPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseUpdatedPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(updatedPaymentTransaction.getVendor().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

    }

    @Test
    void updateVendorInternalServerErr() {

        PaymentTransaction updatedPaymentTransaction = paymentTransaction1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(updatedPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransaction>> responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        ResponseMessage<PaymentTransaction> responseMessage = (ResponseMessage<PaymentTransaction>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransaction responseUpdatedPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseUpdatedPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(updatedPaymentTransaction.getVendor().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

    }
}