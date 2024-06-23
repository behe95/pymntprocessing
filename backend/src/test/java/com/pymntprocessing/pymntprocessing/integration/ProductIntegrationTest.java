package com.pymntprocessing.pymntprocessing.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
import com.pymntprocessing.pymntprocessing.model.mapper.ProductMapper;
import com.pymntprocessing.pymntprocessing.util.DatabaseDateTimeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    private DatabaseDateTimeConverter databaseDateTimeConverter;

    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    public void setup() {
        databaseInitializer.init();
    }

    @Test
    void getProductById() throws Exception {
        DatabaseInitializer.product1.setCreated(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.product1.getCreated()));
        DatabaseInitializer.product1.setModified(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.product1.getModified()));

        ResponsePayload<ProductDTO> responsePayload = new ResponsePayload<>(
                this.productMapper.convertToDTO(DatabaseInitializer.product1)
                ,true
                ,""
        );
        String JSONResponse = this.objectMapper.writeValueAsString(responsePayload);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponse));
    }



    @Test
    void getAllProductWhenExist() throws Exception {
        DatabaseInitializer.product1.setCreated(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.product1.getCreated()));
        DatabaseInitializer.product1.setModified(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.product1.getModified()));

        DatabaseInitializer.product2.setCreated(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.product2.getCreated()));
        DatabaseInitializer.product2.setModified(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.product2.getModified()));


        ResponsePayload<List<ProductDTO>> responsePayload = new ResponsePayload<>(
                List.of(
                        this.productMapper.convertToDTO(DatabaseInitializer.product1)
                        ,this.productMapper.convertToDTO(DatabaseInitializer.product2)
                )
                ,true
                ,""
        );
        String JSONResponse = this.objectMapper.writeValueAsString(responsePayload);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponse));
    }

    @Test
    void createProduct() throws Exception {
        ProductDTO newProductDTO = new ProductDTO();
        newProductDTO.setId(null);
        newProductDTO.setProductName("Product One");
        newProductDTO.setProductDescription("Product One Description");
        newProductDTO.setCreated(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.datetime));
        newProductDTO.setModified(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.datetime));
        newProductDTO.setPaymentTransactionDTOs(null);

        ProductDTO createdProductDTO = new ProductDTO();
        this.modelMapper.map(newProductDTO, createdProductDTO);
        createdProductDTO.setPaymentTransactionDTOs(List.of());
        createdProductDTO.setId(3L);

        String JSONRequest = this.objectMapper.writeValueAsString(newProductDTO);

        ResponsePayload<ProductDTO> responsePayloadOnCreate = new ResponsePayload<>(
                createdProductDTO
                ,true
                ,"Product created!"
        );
        String JSONResponseOnCreate = this.objectMapper.writeValueAsString(responsePayloadOnCreate);

        /**
         * Create product
         */
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONRequest)
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnCreate));

        /**
         * Query from Database
         */

        ResponsePayload<ProductDTO> responsePayloadAfterCreate = new ResponsePayload<>(
                createdProductDTO
                ,true
                ,""
        );
        String JSONResponseAfterCreate = this.objectMapper.writeValueAsString(responsePayloadAfterCreate);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseAfterCreate));

    }



    @Test
    void createProductWithNewPaymentTransaction() throws Exception {


        PaymentTransactionDTO newPaymentTransactionDTO = new PaymentTransactionDTO();
        newPaymentTransactionDTO.setId(null);
        newPaymentTransactionDTO.setProductDTO(null);
        newPaymentTransactionDTO.setVendor(DatabaseInitializer.vendor1);
        newPaymentTransactionDTO.setTransactionType(DatabaseInitializer.transactionType);
        newPaymentTransactionDTO.setTransactionNumber(2);
        newPaymentTransactionDTO.setTransactionDescription("Project One - 1/3");
        newPaymentTransactionDTO.setTransactionAmount(5500.16);


        PaymentTransactionDTO createdPaymentTransactionDTO = new PaymentTransactionDTO();
        this.modelMapper.map(newPaymentTransactionDTO, createdPaymentTransactionDTO);
        createdPaymentTransactionDTO.setId(2L);


        ProductDTO newProductDTO = new ProductDTO();
        newProductDTO.setId(null);
        newProductDTO.setProductName("Product One");
        newProductDTO.setProductDescription("Product One Description");
        newProductDTO.setCreated(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.datetime));
        newProductDTO.setModified(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.datetime));
        newProductDTO.setPaymentTransactionDTOs(null);

        newProductDTO.setPaymentTransactionDTOs(List.of(newPaymentTransactionDTO));

        ProductDTO createdProductDTO = new ProductDTO();
        this.modelMapper.map(newProductDTO, createdProductDTO);
        createdProductDTO.setId(3L);
        createdProductDTO.setPaymentTransactionDTOs(List.of(createdPaymentTransactionDTO));

        String JSONRequest = this.objectMapper.writeValueAsString(newProductDTO);

        ResponsePayload<ProductDTO> responsePayloadOnCreate = new ResponsePayload<>(
                createdProductDTO
                ,true
                ,"Product created!"
        );
        String JSONResponseOnCreate = this.objectMapper.writeValueAsString(responsePayloadOnCreate);

        /**
         * Create product
         */
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONRequest)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnCreate));

        /**
         * Query from Database
         */

        ResponsePayload<ProductDTO> responsePayloadAfterCreate = new ResponsePayload<>(
                createdProductDTO
                ,true
                ,""
        );
        String JSONResponseAfterCreate = this.objectMapper.writeValueAsString(responsePayloadAfterCreate);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseAfterCreate));

    }


    /**
     * TODO     Fix update to untie product from all the transactions
     *          when empty list provided
     */
    @Test
    void updateProduct() throws Exception {
        ProductDTO newProductDTO = new ProductDTO();
        newProductDTO.setId(1L);
        newProductDTO.setProductName("Product One");
        newProductDTO.setProductDescription("Product One Description Updated");
        newProductDTO.setCreated(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.datetime));
        newProductDTO.setModified(this.databaseDateTimeConverter.convertTo_yyyyMMdd_HHmmss(DatabaseInitializer.datetime));
        newProductDTO.setPaymentTransactionDTOs(List.of());

        ProductDTO updatedProductDTO = new ProductDTO();
        this.modelMapper.map(newProductDTO, updatedProductDTO);

        String JSONRequest = this.objectMapper.writeValueAsString(newProductDTO);

        ResponsePayload<ProductDTO> responsePayloadOnUpdate = new ResponsePayload<>(
                updatedProductDTO
                ,true
                ,"Product updated!"
        );
        String JSONResponseOnUpdate = this.objectMapper.writeValueAsString(responsePayloadOnUpdate);

        /**
         * Update product
         */
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/product/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONRequest)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnUpdate));

        /**
         * Query from Database
         */

        ResponsePayload<ProductDTO> responsePayloadAfterUpdate = new ResponsePayload<>(
                updatedProductDTO
                ,true
                ,""
        );
        String JSONResponseAfterUpdate = this.objectMapper.writeValueAsString(responsePayloadAfterUpdate);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseAfterUpdate));

    }
}
