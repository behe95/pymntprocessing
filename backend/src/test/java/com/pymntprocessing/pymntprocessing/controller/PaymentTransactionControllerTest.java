package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.entity.*;
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

    private PaymentTransactionDTO paymentTransactionDTO1;
    private PaymentTransactionDTO paymentTransactionDTO2;
//    TransactionStatus transactionStatus;
    TransactionType transactionType;

    Vendor vendor1;

    @BeforeEach
    void setUp() {
//        transactionStatus = new TransactionStatus(1L, "Open", 1);
        transactionType = new TransactionType(2L, "Debit", 1, 2);

        vendor1 = new Vendor();
        vendor1.setId(1L);
        vendor1.setName("Vendor One");
        vendor1.setAddress("11 Long Ave, NYC");
        vendor1.setVendorId("EVID0001");

        paymentTransactionDTO1 = new PaymentTransactionDTO();
        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(vendor1);
        paymentTransactionDTO1.setTransactionType(transactionType);
//        paymentTransactionDTO1.setTransactionStatus(transactionStatus);
        paymentTransactionDTO1.setTransactionNumber(1);
        paymentTransactionDTO1.setTransactionDescription("Payment Transaction Description 1");
        paymentTransactionDTO1.setTransactionAmount(50.50);


        paymentTransactionDTO2 = new PaymentTransactionDTO();
        paymentTransactionDTO2.setId(2L);
        paymentTransactionDTO2.setVendor(vendor1);
        paymentTransactionDTO2.setTransactionType(transactionType);
//        paymentTransactionDTO2.setTransactionStatus(transactionStatus);
        paymentTransactionDTO2.setTransactionNumber(2);
        paymentTransactionDTO2.setTransactionDescription("Payment Transaction Description 2");
        paymentTransactionDTO2.setTransactionAmount(150.00);
    }

    @Test
    void getAllPaymentTransactionWhenExist() {
        // given
        given(this.paymentTransactionService.getAllPaymentTransaction()).willReturn(List.of(paymentTransactionDTO1, paymentTransactionDTO2));

        // when
        ResponseEntity<ResponseMessage<List<PaymentTransactionDTO>>> responseEntity = this.paymentTransactionController.getAllPaymentTransaction();

        // then
        ResponseMessage<List<PaymentTransactionDTO>> responseMessage = (ResponseMessage<List<PaymentTransactionDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransactionDTO> paymentTransactions = responseMessage.getData();

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
        ResponseEntity<ResponseMessage<List<PaymentTransactionDTO>>> responseEntity = this.paymentTransactionController.getAllPaymentTransaction();

        // then
        ResponseMessage<List<PaymentTransactionDTO>> responseMessage = (ResponseMessage<List<PaymentTransactionDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransactionDTO> paymentTransactions = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, paymentTransactions.size());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransaction();
    }

    @Test
    void getPaymentTransactionByIdWhenFound() {
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(1L)).willReturn(paymentTransactionDTO1);

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.getPaymentTransactionById(1L);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO responsePaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(paymentTransactionDTO1, responsePaymentTransaction);
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(1L);
    }

    @Test
    void getPaymentTransactionByIdWhenNotFound() {
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(11L)).willReturn(null);

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.getPaymentTransactionById(11L);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert  responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO responsePaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responsePaymentTransaction);
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(11L);

    }

    @Test
    void getAllPaymentTransactionByVendorIdWhenExist() {
        // given
        given(this.paymentTransactionService.getAllPaymentTransactionByVendorId(1L)).willReturn(List.of(paymentTransactionDTO1, paymentTransactionDTO2));

        // when
        ResponseEntity<ResponseMessage<List<PaymentTransactionDTO>>> responseEntity = this.paymentTransactionController.getAllPaymentTransactionByVendorId(1L);

        // then
        ResponseMessage<List<PaymentTransactionDTO>> responseMessage = (ResponseMessage<List<PaymentTransactionDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransactionDTO> responsePaymentTransactions = responseMessage.getData();

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
        ResponseEntity<ResponseMessage<List<PaymentTransactionDTO>>> responseEntity = this.paymentTransactionController.getAllPaymentTransactionByVendorId(1L);

        // then
        ResponseMessage<List<PaymentTransactionDTO>> responseMessage = (ResponseMessage<List<PaymentTransactionDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<PaymentTransactionDTO> responsePaymentTransactions = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, responsePaymentTransactions.size());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransactionByVendorId(1L);
    }

    @Test
    void createPaymentTransactionWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity;
        ResponseMessage<PaymentTransactionDTO> responseMessage;
        /**
         * Vendor not provided
         */
        PaymentTransactionDTO newPaymentTransaction = paymentTransactionDTO1;
        newPaymentTransaction.setVendor(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));

//        /**
//         * Transaction status not provided
//         */
//        newPaymentTransaction.setVendor(vendor1);
////        newPaymentTransaction.setTransactionStatus(null);
//
//        // when
//        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);
//
//        // then
//        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
//        assert responseMessage != null;
//        isSuccess = responseMessage.isSuccess();
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertFalse(isSuccess);
//        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
////        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));

        /**
         * Transaction type not provided
         */
        newPaymentTransaction.setVendor(vendor1);
//        newPaymentTransaction.setTransactionStatus(transactionStatus);
        newPaymentTransaction.setTransactionType(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));
    }

    @Test
    void createPaymentTransactionWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity;
        ResponseMessage<PaymentTransactionDTO> responseMessage;
        /**
         * Invalid vendor provided
         */
        PaymentTransactionDTO newPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));

        reset(vendorService);
//        /**
//         * Invalid Transaction status provided
//         */
//
//        // given
//        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
////        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getId())).willReturn(null);
//
//        // when
//        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);
//
//        // then
//        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
//        assert responseMessage != null;
//        isSuccess = responseMessage.isSuccess();
//        responsePaymentTransaction = responseMessage.getData();
//
//        assertNull(responsePaymentTransaction);
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertFalse(isSuccess);
//        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
////        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));
//
//
//        reset(vendorService);
//        reset(paymentTransactionService);

        /**
         * Invalid Transaction type provided
         */

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));
    }




    @Test
    void createPaymentTransaction() {

        PaymentTransactionDTO newPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(newPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.createPaymentTransaction(newPaymentTransaction)).willReturn(newPaymentTransaction);

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO createdPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(newPaymentTransaction, createdPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(newPaymentTransaction.getVendor().getId());
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(newPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(newPaymentTransaction);

    }
    @Test
    void createVendorDuplicate() {

        PaymentTransactionDTO newPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(newPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.createPaymentTransaction(newPaymentTransaction)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO createdPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(newPaymentTransaction.getVendor().getId());
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(newPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(newPaymentTransaction);

    }

    @Test
    void createVendorInternalServerErr() {

        PaymentTransactionDTO newPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(newPaymentTransaction.getVendor().getId())).willReturn(newPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId())).willReturn(newPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(newPaymentTransaction.getTransactionType().getId())).willReturn(newPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.createPaymentTransaction(newPaymentTransaction)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.createPaymentTransaction(newPaymentTransaction);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO createdPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(newPaymentTransaction.getVendor().getId());
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(newPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(newPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(newPaymentTransaction);

    }




    @Test
    void updatePaymentTransactionWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity;
        ResponseMessage<PaymentTransactionDTO> responseMessage;
        /**
         * Vendor not provided
         */
        PaymentTransactionDTO updatedPaymentTransaction = paymentTransactionDTO1;
        updatedPaymentTransaction.setVendor(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class),any(PaymentTransactionDTO.class));

//        /**
//         * Transaction status not provided
//         */
//        updatedPaymentTransaction.setVendor(vendor1);
////        updatedPaymentTransaction.setTransactionStatus(null);
//
//        // when
//        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);
//
//        // then
//        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
//        assert responseMessage != null;
//        isSuccess = responseMessage.isSuccess();
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertFalse(isSuccess);
//        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
////        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class),any(PaymentTransactionDTO.class));

        /**
         * Transaction type not provided
         */
        updatedPaymentTransaction.setVendor(vendor1);
//        updatedPaymentTransaction.setTransactionStatus(transactionStatus);
        updatedPaymentTransaction.setTransactionType(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));
    }

    @Test
    void updatePaymentTransactionWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity;
        ResponseMessage<PaymentTransactionDTO> responseMessage;
        /**
         * Invalid vendor provided
         */
        PaymentTransactionDTO updatedPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));

        reset(vendorService);
//        /**
//         * Invalid Transaction status provided
//         */
//
//        // given
//        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
////        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getId())).willReturn(null);
//
//        // when
//        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);
//
//        // then
//        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
//        assert responseMessage != null;
//        isSuccess = responseMessage.isSuccess();
//        responsePaymentTransaction = responseMessage.getData();
//
//        assertNull(responsePaymentTransaction);
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        assertFalse(isSuccess);
//        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
////        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).getTransactionTypeById(any(Long.class));
//        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));
//
//
//        reset(vendorService);
//        reset(paymentTransactionService);

        /**
         * Invalid Transaction type provided
         */

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(null);

        // when
        responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responsePaymentTransaction = responseMessage.getData();

        assertNull(responsePaymentTransaction);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(any(Long.class));
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));
    }




    @Test
    void updatePaymentTransaction() {

        PaymentTransactionDTO updatedPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(updatedPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction)).willReturn(updatedPaymentTransaction);

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO responseUpdatedPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(updatedPaymentTransaction, responseUpdatedPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(updatedPaymentTransaction.getVendor().getId());
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

    }
    @Test
    void updatedVendorDuplicate() {

        PaymentTransactionDTO updatedPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(updatedPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO responseUpdatedPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseUpdatedPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(updatedPaymentTransaction.getVendor().getId());
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

    }

    @Test
    void updateVendorInternalServerErr() {

        PaymentTransactionDTO updatedPaymentTransaction = paymentTransactionDTO1;

        // given
        given(this.vendorService.getVendorById(updatedPaymentTransaction.getVendor().getId())).willReturn(updatedPaymentTransaction.getVendor());
//        given(this.paymentTransactionService.getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId())).willReturn(updatedPaymentTransaction.getTransactionStatus());
        given(this.paymentTransactionService.getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId())).willReturn(updatedPaymentTransaction.getTransactionType());
        given(this.paymentTransactionService.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        PaymentTransactionDTO responseUpdatedPaymentTransaction = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseUpdatedPaymentTransaction);
        verify(this.vendorService, times(1)).getVendorById(updatedPaymentTransaction.getVendor().getId());
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(updatedPaymentTransaction.getTransactionStatus().getId());
        verify(this.paymentTransactionService, times(1)).getTransactionTypeById(updatedPaymentTransaction.getTransactionType().getId());
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(updatedPaymentTransaction.getId(), updatedPaymentTransaction);

    }

    @Test
    void deletePaymentTransaction() {
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(paymentTransactionDTO1.getId())).willReturn(paymentTransactionDTO1);
//        given(this.paymentTransactionService.getTransactionStatusById(paymentTransactionDTO1.getTransactionStatus().getId())).willReturn(paymentTransactionDTO1.getTransactionStatus());


        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.deletePaymentTransaction(paymentTransactionDTO1.getId());

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertTrue(isSuccess);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(paymentTransactionDTO1.getId());
//        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(paymentTransactionDTO1.getId());
        verify(this.paymentTransactionService, times(1)).deletePaymentTransaction(paymentTransactionDTO1.getId());
    }


//    @Test
//    void deletePaymentTransactionWhenPaid() {
////        TransactionStatus transactionStatus1 = new TransactionStatus(1L, "Paid", 5);
////        paymentTransactionDTO1.setTransactionStatus(transactionStatus1);
//        // given
//        given(this.paymentTransactionService.getPaymentTransactionById(paymentTransactionDTO1.getId())).willReturn(paymentTransactionDTO1);
////        given(this.paymentTransactionService.getTransactionStatusById(paymentTransactionDTO1.getTransactionStatus().getId())).willReturn(paymentTransactionDTO1.getTransactionStatus());
//
//
//        // when
//        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.deletePaymentTransaction(paymentTransactionDTO1.getId());
//
//        // then
//        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
//        assert responseMessage != null;
//        boolean isSuccess = responseMessage.isSuccess();
//        assertFalse(isSuccess);
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(paymentTransactionDTO1.getId());
////        verify(this.paymentTransactionService, times(1)).getTransactionStatusById(paymentTransactionDTO1.getId());
//        verify(this.paymentTransactionService, times(0)).deletePaymentTransaction(paymentTransactionDTO1.getId());
//    }

    @Test
    void deletePaymentTransactionWithNonExistingId() {
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(paymentTransactionDTO1.getId())).willReturn(null);


        // when
        ResponseEntity<ResponseMessage<PaymentTransactionDTO>> responseEntity = this.paymentTransactionController.deletePaymentTransaction(paymentTransactionDTO1.getId());

        // then
        ResponseMessage<PaymentTransactionDTO> responseMessage = (ResponseMessage<PaymentTransactionDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertFalse(isSuccess);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(paymentTransactionDTO1.getId());
//        verify(this.paymentTransactionService, times(0)).getTransactionStatusById(paymentTransactionDTO1.getId());
        verify(this.paymentTransactionService, times(0)).deletePaymentTransaction(paymentTransactionDTO1.getId());
    }
}