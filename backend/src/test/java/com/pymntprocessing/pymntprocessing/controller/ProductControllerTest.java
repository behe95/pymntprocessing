package com.pymntprocessing.pymntprocessing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pymntprocessing.pymntprocessing.exception.GlobalErrorException;
import com.pymntprocessing.pymntprocessing.exception.ProductAssignedWithInvalidPaymentTransactionException;
import com.pymntprocessing.pymntprocessing.exception.ProductNotFoundException;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import com.pymntprocessing.pymntprocessing.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private PaymentTransactionService paymentTransactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

        productDTO2.setPaymentTransactionDTOs(List.of(paymentTransactionDTO2));
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
        .andExpect(jsonPath("$.payload.id").value(id))
                .andExpect(jsonPath("$.payload.paymentTransactionDTOs").value(productDTO1.getPaymentTransactionDTOs()))
                .andExpect(jsonPath("$.payload.productName").value(productDTO1.getProductName()))
                .andExpect(jsonPath("$.payload.productDescription").value(productDTO1.getProductDescription()))
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
        given(this.productService.getProductById(any(Long.class))).willThrow(new ProductNotFoundException());

        String expectedReturnJSON = this.objectMapper.writeValueAsString(
                new ResponsePayload<GlobalErrorException>(new ProductNotFoundException(), false, "Product doesn't exist!")
        );

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/product/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        ArgumentCaptor<Long> productArgumentCaptorId = ArgumentCaptor.forClass(Long.class);
        then(this.productService).should(times(1)).getProductById(productArgumentCaptorId.capture());
        assertEquals(id, productArgumentCaptorId.getValue());
    }

    @Test
    void getAllProductWhenExist() throws Exception {
        // given
        given(this.productService.getAllProducts()).willReturn(List.of(productDTO1, productDTO2));

        String expectedReturnJSON = this.objectMapper.writeValueAsString(
                new ResponsePayload<>(List.of(productDTO1, productDTO2), true, "")
        );

        // when
        // then
        MvcResult mvcResult =
            this.mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/product")
                            .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedReturnJSON))
            .andReturn();

        verify(this.productService, times(1)).getAllProducts();
    }

    @Test
    void getAllProductWhenNotExist() throws Exception {
        // given
        given(this.productService.getAllProducts()).willReturn(List.of());


        String expectedReturnJSON = this.objectMapper.writeValueAsString(
                new ResponsePayload<>(List.of(), true, "")
        );
        // when
        // then
        MvcResult mvcResult =
                this.mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/v1/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                        ).andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.payload", hasSize(0)))
                        .andExpect(content().json(expectedReturnJSON))
                        .andReturn();


        verify(this.productService, times(1)).getAllProducts();
    }

    @Test
    void createProductWhenPaymentTransactionNotExist() throws Exception {
        ProductDTO newProductDTO = productDTO1;
        newProductDTO.setId(null);
        newProductDTO.setPaymentTransactionDTOs(List.of());
        String JSONRequestBody = this.objectMapper.writeValueAsString(newProductDTO);

        ProductDTO returnProductDTO = new ProductDTO();
        returnProductDTO.setId(1L);
        returnProductDTO.setProductName(newProductDTO.getProductName());
        returnProductDTO.setProductDescription(newProductDTO.getProductDescription());
        returnProductDTO.setCreated(newProductDTO.getCreated());
        returnProductDTO.setModified(newProductDTO.getModified());

        String expectedReturnJSON = this.objectMapper.writeValueAsString(
            new ResponsePayload<ProductDTO>(returnProductDTO, true, "Product created!")
        );


        // given
        given(this.productService.createProduct(any(ProductDTO.class))).willReturn(returnProductDTO);

        // when
        // then
        MvcResult mvcResult =
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(JSONRequestBody)
                        ).andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.content().json(expectedReturnJSON))
                        .andReturn();

        verify(this.paymentTransactionService, times(0)).getPaymentTransactionById(any(Long.class));

        ArgumentCaptor<ProductDTO> productDTOArgumentCaptor = ArgumentCaptor.forClass(ProductDTO.class);

        verify(this.productService, times(1)).createProduct(productDTOArgumentCaptor.capture());

        ProductDTO requestBodyProductDTO = productDTOArgumentCaptor.getValue();
        assertEquals(newProductDTO.getPaymentTransactionDTOs().size(), requestBodyProductDTO.getPaymentTransactionDTOs().size());
        assertEquals(newProductDTO.getProductName(), requestBodyProductDTO.getProductName());
        assertEquals(newProductDTO.getProductDescription(), requestBodyProductDTO.getProductDescription());
        assertEquals(newProductDTO.getId(), requestBodyProductDTO.getId());
        assertEquals(newProductDTO.getCreated(), requestBodyProductDTO.getCreated());
        assertEquals(newProductDTO.getModified(), requestBodyProductDTO.getModified());
    }

    @Test
    void createProductWhenPaymentTransactionExistWithoutProductDTO() throws Exception {
        ProductDTO newProductDTO = productDTO1;
        newProductDTO.setId(null);

        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionDTO2;
        paymentTransactionDTO.setId(1L);
        paymentTransactionDTO.setProductDTO(null);

        newProductDTO.setPaymentTransactionDTOs(List.of(paymentTransactionDTO));



        String JSONRequestBody = this.objectMapper.writeValueAsString(newProductDTO);

        ProductDTO returnProductDTO = new ProductDTO();
        returnProductDTO.setId(1L);
        returnProductDTO.setProductName(newProductDTO.getProductName());
        returnProductDTO.setProductDescription(newProductDTO.getProductDescription());
        returnProductDTO.setCreated(newProductDTO.getCreated());
        returnProductDTO.setModified(newProductDTO.getModified());

        String expectedReturnJSON = this.objectMapper.writeValueAsString(
                new ResponsePayload<ProductDTO>(returnProductDTO, true, "Product created!")
        );



        // given
        given(this.productService.createProduct(any(ProductDTO.class))).willReturn(returnProductDTO);

        // when
        // then
        MvcResult mvcResult =
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(JSONRequestBody)
                        ).andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.content().json(expectedReturnJSON))
                        .andReturn();


        ArgumentCaptor<ProductDTO> productDTOArgumentCaptor = ArgumentCaptor.forClass(ProductDTO.class);

        verify(this.productService, times(1)).createProduct(productDTOArgumentCaptor.capture());

        ProductDTO requestBodyProductDTO = productDTOArgumentCaptor.getValue();
        assertEquals(newProductDTO.getProductName(), requestBodyProductDTO.getProductName());
        assertEquals(newProductDTO.getProductDescription(), requestBodyProductDTO.getProductDescription());
        assertEquals(newProductDTO.getId(), requestBodyProductDTO.getId());
        assertEquals(newProductDTO.getCreated(), requestBodyProductDTO.getCreated());
        assertEquals(newProductDTO.getModified(), requestBodyProductDTO.getModified());

    }

    @Test
    void createProductWhenPaymentTransactionExistWithAlreadyExistingProductDTO() throws Exception {
        ProductDTO newProductDTO = productDTO1;
        newProductDTO.setId(null);

        productDTO2.setPaymentTransactionDTOs(List.of());
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionDTO2;
        paymentTransactionDTO.setId(1L);
        paymentTransactionDTO.setProductDTO(productDTO2);

        newProductDTO.setPaymentTransactionDTOs(List.of(paymentTransactionDTO));

        String JSONRequestBody = this.objectMapper.writeValueAsString(newProductDTO);

        // given
        given(this.productService.createProduct(any(ProductDTO.class))).willThrow(new ProductAssignedWithInvalidPaymentTransactionException());

        // when
        // then
        MvcResult mvcResult =
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(JSONRequestBody)
                        ).andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Unable to assign payment transaction to product"))
                        .andExpect(jsonPath("$.success").value(false))
                        .andReturn();

        verify(this.productService, times(1)).createProduct(any(ProductDTO.class));

    }

    @Test
    void createProductWhenPaymentTransactionWihtDuplicateEntryException() throws Exception {
        ProductDTO newProductDTO = productDTO1;
        newProductDTO.setId(null);
        newProductDTO.setPaymentTransactionDTOs(List.of());



        String JSONRequestBody = this.objectMapper.writeValueAsString(newProductDTO);

        // given
        given(this.productService.createProduct(any(ProductDTO.class))).willThrow(new DataIntegrityViolationException(""));

        // when
        // then
        MvcResult mvcResult =
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(JSONRequestBody)
                        ).andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("ERROR: Duplicate entry!"))
                        .andExpect(jsonPath("$.success").value(false))
                        .andReturn();

        verify(this.productService, times(1)).createProduct(any(ProductDTO.class));

    }

    @Test
    void createProductWhenPaymentTransactionWihtInternalServerErrorException() throws Exception {
        ProductDTO newProductDTO = productDTO1;
        newProductDTO.setId(null);
        newProductDTO.setPaymentTransactionDTOs(List.of());



        String JSONRequestBody = this.objectMapper.writeValueAsString(newProductDTO);



        String expectedReturnJSON = this.objectMapper.writeValueAsString(
                new ResponsePayload<ProductDTO>(null, false, "Something went wrong! Additional error message")
        );

        // given
        given(this.productService.createProduct(any(ProductDTO.class))).willThrow(new RuntimeException("Additional error message"));

        // when
        // then
        MvcResult mvcResult =
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(JSONRequestBody)
                        ).andDo(print())
                        .andExpect(status().isInternalServerError())
                        .andExpect(content().json(expectedReturnJSON))
                        .andReturn();

        verify(this.productService, times(1)).createProduct(any(ProductDTO.class));

    }


    @Test
    void updateProductWithNoIdProvided() throws Exception {
        productDTO1.setId(null);

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false, "Product id not provided!"));
        String requestBody = this.objectMapper.writeValueAsString(productDTO1);

        // when
        // then
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.productService, times(0)).getProductById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getPaymentTransactionById(any(Long.class));
        verify(this.productService, times(0)).updateProduct(any(Long.class), any(ProductDTO.class));
    }


    @Test
    void updateProductWhenProductNotExist() throws Exception {
        productDTO1.setId(1L);

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false, "Product doesn't exist!"));
        String requestBody = this.objectMapper.writeValueAsString(productDTO1);

        // given
        given(this.productService.getProductById(any(Long.class))).willReturn(null);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.productService, times(1)).getProductById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getPaymentTransactionById(any(Long.class));
        verify(this.productService, times(0)).updateProduct(any(Long.class), any(ProductDTO.class));
    }


    @Test
    void updateProductWhenPaymentTransactionAssignedAlreadyHasProductTied() throws Exception {
        productDTO1.setId(1L);
        paymentTransactionDTO2.setId(1L);
        productDTO2.setPaymentTransactionDTOs(List.of());
        paymentTransactionDTO2.setProductDTO(productDTO2);
        productDTO1.setPaymentTransactionDTOs(List.of(paymentTransactionDTO2));

        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(new ProductAssignedWithInvalidPaymentTransactionException(), false,
                "Unable to assign payment transaction to product"));
        String requestBody = this.objectMapper.writeValueAsString(productDTO1);


        ProductDTO existingProductDTO = new ProductDTO();
        existingProductDTO.setId(1L);
        existingProductDTO.setProductName(productDTO1.getProductName());
        existingProductDTO.setProductDescription(productDTO1.getProductDescription());
        existingProductDTO.setCreated(productDTO1.getCreated());
        existingProductDTO.setModified(productDTO1.getModified());

        // given
        given(this.productService.getProductById(any(Long.class))).willReturn(existingProductDTO);
        given(this.productService.updateProduct(any(Long.class), any(ProductDTO.class))).willThrow(new ProductAssignedWithInvalidPaymentTransactionException());

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));
        verify(this.productService, times(1)).getProductById(any(Long.class));
        verify(this.productService, times(1)).updateProduct(any(Long.class), any(ProductDTO.class));
    }


    @Test
    void updateProduct() throws Exception {
        productDTO1.setId(1L);
        paymentTransactionDTO2.setId(1L);
        productDTO2.setPaymentTransactionDTOs(List.of());
        paymentTransactionDTO2.setProductDTO(null);
        productDTO1.setPaymentTransactionDTOs(List.of(paymentTransactionDTO2));


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(productDTO1, true,"Product updated!"));
        String requestBody = this.objectMapper.writeValueAsString(productDTO1);


        ProductDTO existingProductDTO = new ProductDTO();
        existingProductDTO.setId(1L);
        existingProductDTO.setProductName(productDTO1.getProductName());
        existingProductDTO.setProductDescription(productDTO1.getProductDescription());
        existingProductDTO.setCreated(productDTO1.getCreated());
        existingProductDTO.setModified(productDTO1.getModified());

        // given
        given(this.productService.getProductById(any(Long.class))).willReturn(existingProductDTO);
        given(this.productService.updateProduct(any(Long.class), any(ProductDTO.class))).willReturn(productDTO1);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        verify(this.productService, times(1)).getProductById(any(Long.class));
        verify(this.productService, times(1)).updateProduct(any(Long.class), any(ProductDTO.class));
    }


    @Test
    void updateProductWithDataIntegrityViolationException() throws Exception {
        productDTO1.setId(1L);
        paymentTransactionDTO2.setId(1L);
        productDTO2.setPaymentTransactionDTOs(List.of());
        paymentTransactionDTO2.setProductDTO(null);
        productDTO1.setPaymentTransactionDTOs(List.of(paymentTransactionDTO2));


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false,"ERROR: Duplicate entry!"));
        String requestBody = this.objectMapper.writeValueAsString(productDTO1);


        ProductDTO existingProductDTO = new ProductDTO();
        existingProductDTO.setId(1L);
        existingProductDTO.setProductName(productDTO1.getProductName());
        existingProductDTO.setProductDescription(productDTO1.getProductDescription());
        existingProductDTO.setCreated(productDTO1.getCreated());
        existingProductDTO.setModified(productDTO1.getModified());

        // given
        given(this.productService.getProductById(any(Long.class))).willReturn(existingProductDTO);
        given(this.productService.updateProduct(any(Long.class), any(ProductDTO.class))).willThrow(new DataIntegrityViolationException(""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        verify(this.productService, times(1)).getProductById(any(Long.class));
        verify(this.productService, times(1)).updateProduct(any(Long.class), any(ProductDTO.class));
    }


    @Test
    void updateProductWithInternalServerException() throws Exception {
        productDTO1.setId(1L);
        paymentTransactionDTO2.setId(1L);
        productDTO2.setPaymentTransactionDTOs(List.of());
        paymentTransactionDTO2.setProductDTO(null);
        productDTO1.setPaymentTransactionDTOs(List.of());


        String expectedReturnJSON = this.objectMapper.writeValueAsString(
                new ResponsePayload<ProductDTO>(null, false, "Something went wrong! Additional error message")
        );
        String requestBody = this.objectMapper.writeValueAsString(productDTO1);


        ProductDTO existingProductDTO = new ProductDTO();
        existingProductDTO.setId(1L);
        existingProductDTO.setProductName(productDTO1.getProductName());
        existingProductDTO.setProductDescription(productDTO1.getProductDescription());
        existingProductDTO.setCreated(productDTO1.getCreated());
        existingProductDTO.setModified(productDTO1.getModified());

        // given
        given(this.productService.getProductById(any(Long.class))).willReturn(existingProductDTO);
        given(this.productService.updateProduct(any(Long.class), any(ProductDTO.class))).willThrow(new RuntimeException("Additional error message"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                ).andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        verify(this.productService, times(1)).getProductById(any(Long.class));
        verify(this.productService, times(1)).updateProduct(any(Long.class), any(ProductDTO.class));
    }

    @Test
    void deletePaymentTransactionWithProductExist() throws Exception {

        productDTO1.setId(1L);
        paymentTransactionDTO2.setId(1L);
        productDTO2.setPaymentTransactionDTOs(List.of());
        paymentTransactionDTO2.setProductDTO(null);
        productDTO1.setPaymentTransactionDTOs(List.of(paymentTransactionDTO2));


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, false,"Product doesn't exist!"));


        // given
        given(this.productService.getProductById(any(Long.class))).willReturn(null);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        verify(this.productService, times(1)).getProductById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getPaymentTransactionById(any(Long.class));
        verify(this.productService, times(0)).deleteProduct(any(Long.class));
    }

    @Test
    void deleteProduct() throws Exception {


        productDTO1.setId(1L);
        paymentTransactionDTO2.setId(1L);
        productDTO2.setPaymentTransactionDTOs(List.of());
        paymentTransactionDTO2.setProductDTO(null);
        productDTO1.setPaymentTransactionDTOs(List.of(paymentTransactionDTO2));


        String expectedResponseJSON = this.objectMapper.writeValueAsString(new ResponsePayload<>(null, true,"Product " + productDTO1.getProductName() + " has been deleted"));

        ProductDTO existingProductDTO = new ProductDTO();
        existingProductDTO.setId(1L);
        existingProductDTO.setProductName(productDTO1.getProductName());
        existingProductDTO.setProductDescription(productDTO1.getProductDescription());
        existingProductDTO.setPaymentTransactionDTOs(productDTO1.getPaymentTransactionDTOs());
        existingProductDTO.setCreated(productDTO1.getCreated());
        existingProductDTO.setModified(productDTO1.getModified());


        // given
        given(this.productService.getProductById(any(Long.class))).willReturn(existingProductDTO);

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponseJSON));

        verify(this.productService, times(1)).getProductById(any(Long.class));
        verify(this.productService, times(1)).deleteProduct(any(Long.class));
    }
}