package com.pymntprocessing.pymntprocessing.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.exception.PaymentTransactionNotFoundException;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentTransactionController.class)
@AutoConfigureMockMvc
class PaymentTransactionControllerTest {

    @MockBean
    private PaymentTransactionService paymentTransactionService;

    @MockBean
    private VendorService vendorService;
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private PaymentTransactionController paymentTransactionController;

    @Autowired
    private ObjectMapper objectMapper;
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
    void getAllPaymentTransactionWhenExist() throws Exception {
        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO2.setId(2L);
        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(List.of(paymentTransactionDTO1, paymentTransactionDTO2), true, ""));
        // given
        given(this.paymentTransactionService.getAllPaymentTransaction()).willReturn(List.of(paymentTransactionDTO1, paymentTransactionDTO2));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        then(this.paymentTransactionService).should(times(1)).getAllPaymentTransaction();
    }

    @Test
    void getAllPaymentTransactionWhenNotExist() throws Exception {
        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(List.of(), true, ""));
        // given
        given(this.paymentTransactionService.getAllPaymentTransaction()).willReturn(List.of());

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        then(this.paymentTransactionService).should(times(1)).getAllPaymentTransaction();
    }

    @Test
    void getPaymentTransactionByIdWhenFound() throws Exception {
        Long id = 1L;

        paymentTransactionDTO1.setId(id);

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(paymentTransactionDTO1, true, ""));
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(any(Long.class))).willReturn(paymentTransactionDTO1);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/transaction/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        ArgumentCaptor<Long> productArgumentCaptorId = ArgumentCaptor.forClass(Long.class);
        then(this.paymentTransactionService).should(times(1)).getPaymentTransactionById(productArgumentCaptorId.capture());
        assertEquals(id, productArgumentCaptorId.getValue());
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(1L);
    }

    @Test
    void getPaymentTransactionByIdWhenNotFound() throws Exception {
        Long id = 1L;
        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new PaymentTransactionNotFoundException(), false, "Payment transaction doesn't exist!"));
        // given
        given(this.paymentTransactionService.getPaymentTransactionById(any(Long.class))).willThrow(new PaymentTransactionNotFoundException());

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/transaction/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        ArgumentCaptor<Long> productArgumentCaptorId = ArgumentCaptor.forClass(Long.class);
        then(this.paymentTransactionService).should(times(1)).getPaymentTransactionById(productArgumentCaptorId.capture());
        assertEquals(id, productArgumentCaptorId.getValue());
        verify(this.paymentTransactionService, times(1)).getPaymentTransactionById(1L);

    }

    @Test
    void getAllPaymentTransactionByVendorIdWhenExist() throws Exception {

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(List.of(paymentTransactionDTO1, paymentTransactionDTO2), true, ""));

        // given
        given(this.paymentTransactionService.getAllPaymentTransactionByVendorId(any(Long.class))).willReturn(List.of(paymentTransactionDTO1, paymentTransactionDTO2));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/transaction/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransactionByVendorId(1L);
    }



    @Test
    void getAllPaymentTransactionByVendorIdWhenNotExist() throws Exception {

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(List.of(), true, ""));

        // given
        given(this.paymentTransactionService.getAllPaymentTransactionByVendorId(any(Long.class))).willReturn(List.of());

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/transaction/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransactionByVendorId(1L);
    }

    @Test
    void createPaymentTransactionWhenVendorDataNotProvided() throws Exception {
        String errorMessage = "Vendor information not provided!";

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        paymentTransactionDTO1.setVendor(null);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);

        // given
        given(this.paymentTransactionService.createPaymentTransaction(any(PaymentTransactionDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));
    }

    @Test
    void createPaymentTransactionWhenTransactionTypeDataNotProvided() throws Exception {
        String errorMessage = "Transaction type not provided!";

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        paymentTransactionDTO1.setVendor(vendor1);
        paymentTransactionDTO1.setTransactionType(null);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);

        // given
        given(this.paymentTransactionService.createPaymentTransaction(any(PaymentTransactionDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(0)).createPaymentTransaction(any(PaymentTransactionDTO.class));
    }




    @Test
    void createPaymentTransaction() throws Exception {
        paymentTransactionDTO1.setVendor(vendor1);

        transactionType.setId(1L);
        paymentTransactionDTO1.setTransactionType(transactionType);

        paymentTransactionDTO1.setId(null);
        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);


        paymentTransactionDTO1.setId(1L);
        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(paymentTransactionDTO1, true, "Payment transaction created!"));

        // given
        given(this.paymentTransactionService.createPaymentTransaction(any(PaymentTransactionDTO.class))).willReturn(paymentTransactionDTO1);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(any(PaymentTransactionDTO.class));

    }
    @Test
    void createPaymentTransactionWithDuplicateException() throws Exception {
        paymentTransactionDTO1.setVendor(vendor1);

        transactionType.setId(1L);
        paymentTransactionDTO1.setTransactionType(transactionType);

        paymentTransactionDTO1.setId(null);
        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false, "ERROR: Duplicate entry!"));

        // given
        given(this.paymentTransactionService.createPaymentTransaction(any(PaymentTransactionDTO.class))).willThrow(new DataIntegrityViolationException(""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(any(PaymentTransactionDTO.class));

    }

    @Test
    void createPaymentTransactionWithInternalServerErr() throws Exception {
        paymentTransactionDTO1.setVendor(vendor1);

        transactionType.setId(1L);
        paymentTransactionDTO1.setTransactionType(transactionType);

        paymentTransactionDTO1.setId(null);
        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false, "Something went wrong!"));

        // given
        given(this.paymentTransactionService.createPaymentTransaction(any(PaymentTransactionDTO.class))).willThrow(new RuntimeException(""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/transaction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).createPaymentTransaction(any(PaymentTransactionDTO.class));

    }





    @Test
    void updatePaymentTransactionWhenVendorDataNotProvided() throws Exception {
        String errorMessage = "Vendor information not provided!";

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(null);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);

        // given
        given(this.paymentTransactionService.updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class),any(PaymentTransactionDTO.class));
    }

    @Test
    void updatePaymentTransactionWhenInvalidPaymentTransactionDataProvided() throws Exception {
        String errorMessage = "Invalid payment transaction provided!";

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        paymentTransactionDTO1.setId(null);
        paymentTransactionDTO1.setVendor(vendor1);
        paymentTransactionDTO1.setTransactionType(null);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);

        // given
        given(this.paymentTransactionService.updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));
    }

    @Test
    void updatePaymentTransactionWhenTransactionTypeDataNotProvided() throws Exception {
        String errorMessage = "Transaction type not provided!";

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(vendor1);
        paymentTransactionDTO1.setTransactionType(null);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);

        // given
        given(this.paymentTransactionService.updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(0)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));
    }




    @Test
    void updatePaymentTransaction() throws Exception {
        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(vendor1);

        transactionType.setId(1L);
        paymentTransactionDTO1.setTransactionType(transactionType);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(paymentTransactionDTO1, true, "Payment transaction updated!"));

        // given
        given(this.paymentTransactionService.updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class))).willReturn(paymentTransactionDTO1);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));

    }
    @Test
    void updatePaymentTransactionWithDuplicateException() throws Exception {
        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(vendor1);

        transactionType.setId(1L);
        paymentTransactionDTO1.setTransactionType(transactionType);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false, "ERROR: Duplicate entry!"));

        // given
        given(this.paymentTransactionService.updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class))).willThrow(new DataIntegrityViolationException(""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));

    }

    @Test
    void updatePaymentTransactionWithInternalServerErr() throws Exception {
        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(vendor1);

        transactionType.setId(1L);
        paymentTransactionDTO1.setTransactionType(transactionType);

        String requestBodyJSON = this.objectMapper.writeValueAsString(paymentTransactionDTO1);


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false, "Something went wrong!"));

        // given
        given(this.paymentTransactionService.updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class))).willThrow(new RuntimeException(""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyJSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).updatePaymentTransaction(any(Long.class), any(PaymentTransactionDTO.class));

    }

    @Test
    void deletePaymentTransaction() throws Exception {
        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, true, "Payment transaction has been deleted"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).deletePaymentTransaction(any(Long.class));
    }


    @Test
    void deletePaymentTransactionWithNonExistingId() throws Exception {
        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException("Invalid id provided"), false, "Invalid id provided"));

        // given
        doThrow(new InvalidDataProvidedException("Invalid id provided")).when(this.paymentTransactionService).deletePaymentTransaction(any(Long.class));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/transaction/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.paymentTransactionService, times(1)).deletePaymentTransaction(any(Long.class));
    }
}