package com.pymntprocessing.pymntprocessing.integration;

import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PaymentTransactionIntegrationTest {

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    private MockMvc mockMvc;



    @BeforeEach
    public void setup() {
        databaseInitializer.init();
    }

    @Test
    void getAllPaymentTransactionWhenExist() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoice"))

                .andDo(print());
    }
}
