package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.entity.Product;
import com.pymntprocessing.pymntprocessing.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.entity.Vendor;
import com.pymntprocessing.pymntprocessing.service.ProductService;
import com.sun.istack.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    ProductDTO productDTO1;
    ProductDTO productDTO2;
    PaymentTransactionDTO paymentTransactionDTO2;
    TransactionType transactionType;

    Vendor vendor;

    @BeforeEach
    void setUp() {

        transactionType = new TransactionType(2L, "Debit", 1, 2);
        vendor = new Vendor();
        vendor.setId(1L);
        vendor.setName("Vendor One");
        vendor.setAddress("11 Long Ave, NYC");
        vendor.setVendorId("EVID0001");


        productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setProductName("Product One");
        productDTO1.setProductDescription("Product One Description Test");
        productDTO1.setCreated(LocalDateTime.now());
        productDTO1.setModified(null);

        paymentTransactionDTO2 = new PaymentTransactionDTO();
        paymentTransactionDTO2.setId(2L);
        paymentTransactionDTO2.setVendor(vendor);
        paymentTransactionDTO2.setTransactionType(transactionType);
        paymentTransactionDTO2.setTransactionNumber(2);
        paymentTransactionDTO2.setTransactionDescription("Payment Transaction Description 2");
        paymentTransactionDTO2.setTransactionAmount(150.00);

        productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setProductName("Product Two");
        productDTO2.setProductDescription("Product Two Description Test");
        productDTO2.setCreated(LocalDateTime.now().minusDays(11));
        productDTO2.setModified(LocalDateTime.now());

        productDTO2.setPaymentTransactionDTO(paymentTransactionDTO2);
    }

    @Test
    void getProductByIdWhenExist() throws Exception {
        Long id = 1L;
        // given
        given(this.productService.getProductById(id)).willReturn(productDTO1);

        // when
        // then
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/product/"+id)
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.paymentTransactionDTO").value(productDTO1.getPaymentTransactionDTO()))
                .andExpect(jsonPath("$.data.productName").value(productDTO1.getProductName()))
                .andExpect(jsonPath("$.data.productDescription").value(productDTO1.getProductDescription()))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.success").value(true));

        ArgumentCaptor<Long> productArgumentCaptorId = ArgumentCaptor.forClass(Long.class);
        then(this.productService).should(times(1)).getProductById(productArgumentCaptorId.capture());
        assertEquals(id, productArgumentCaptorId.getValue());
    }

    @Test
    void getProductByIdWhenNotExist() throws Exception {
        Long id = 1L;
        // given
        given(this.productService.getProductById(id)).willReturn(null);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/product/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.message").value("Product not found!"))
                .andExpect(jsonPath("$.success").value(false));

        ArgumentCaptor<Long> productArgumentCaptorId = ArgumentCaptor.forClass(Long.class);
        then(this.productService).should(times(1)).getProductById(productArgumentCaptorId.capture());
        assertEquals(id, productArgumentCaptorId.getValue());
    }
}