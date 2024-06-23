package com.pymntprocessing.pymntprocessing.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class VendorIntegrationTest {

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @BeforeEach
    public void setup() {
        databaseInitializer.init();
    }

    @Test
    void getAllVendorWhenExist() throws Exception {

        ResponsePayload<List<Vendor>> responsePayload = new ResponsePayload<>(
                List.of(DatabaseInitializer.vendor1, DatabaseInitializer.vendor2)
                ,true
                ,""
        );
        String JSONResponse = this.objectMapper.writeValueAsString(responsePayload);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponse));
    }

    @Test
    void getAllVendorById() throws Exception {

        ResponsePayload<Vendor> responsePayload = new ResponsePayload<>(
                DatabaseInitializer.vendor1
                ,true
                ,""
        );
        String JSONResponse = this.objectMapper.writeValueAsString(responsePayload);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponse));
    }

    @Test
    void createVendor() throws Exception {


        Vendor newVendor = new Vendor();
        newVendor.setId(null);
        newVendor.setName("New Vendor");
        newVendor.setAddress("New St. , New City, CA, 01100");
        newVendor.setVendorId("VI00003");


        Vendor createdVendor = new Vendor();
        createdVendor.setId(3L);
        createdVendor.setName(newVendor.getName());
        createdVendor.setAddress(newVendor.getAddress());
        createdVendor.setVendorId(newVendor.getVendorId());

        String JSONRequest = this.objectMapper.writeValueAsString(newVendor);

        ResponsePayload<Vendor> responsePayloadOnCreate = new ResponsePayload<>(
                createdVendor
                ,true
                ,"Vendor " + createdVendor.getName() + " created successfully!"
        );
        String JSONResponseOnCreate = this.objectMapper.writeValueAsString(responsePayloadOnCreate);

        /**
         * Create vendor
         */
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/vendor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONRequest)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnCreate));




        /**
         * Query by id
         */

        ResponsePayload<Vendor> responsePayloadAfterCreate = new ResponsePayload<>(
                createdVendor
                ,true
                ,""
        );
        String JSONResponseAfterCreate = this.objectMapper.writeValueAsString(responsePayloadAfterCreate);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseAfterCreate));
    }



    @Test
    void createVendorWithDuplicateVendorId() throws Exception {


        Vendor newVendor = new Vendor();
        newVendor.setId(null);
        newVendor.setName("New Vendor");
        newVendor.setAddress("New St. , New City, CA, 01100");
        newVendor.setVendorId(DatabaseInitializer.vendor1.getVendorId());


        String JSONRequest = this.objectMapper.writeValueAsString(newVendor);
        ResponsePayload<Vendor> responsePayloadOnCreate = new ResponsePayload<>(
                null
                ,false
                ,"ERROR: Duplicate entry! Vendor ID already exists!"
        );
        String JSONResponseOnCreate = this.objectMapper.writeValueAsString(responsePayloadOnCreate);

        /**
         * Create vendor
         */
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/vendor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnCreate));

    }




    @Test
    void updateVendor() throws Exception {

        DatabaseInitializer.vendor1.setName("Updated Vendor");

        String JSONRequest = this.objectMapper.writeValueAsString(DatabaseInitializer.vendor1);

        ResponsePayload<Vendor> responsePayloadOnUpdate = new ResponsePayload<>(
                DatabaseInitializer.vendor1
                ,true
                ,"Vendor " + DatabaseInitializer.vendor1.getName() + " has been updated!"
        );
        String JSONResponseOnUpdate = this.objectMapper.writeValueAsString(responsePayloadOnUpdate);

        /**
         * Update vendor
         */
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONRequest)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnUpdate));




        /**
         * Query by id
         */

        ResponsePayload<Vendor> responsePayloadAfterUpdate = new ResponsePayload<>(
                DatabaseInitializer.vendor1
                ,true
                ,""
        );
        String JSONResponseAfterCreate = this.objectMapper.writeValueAsString(responsePayloadAfterUpdate);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseAfterCreate));
    }



    @Test
    void updateVendorWithDuplicateVendorId() throws Exception {


        DatabaseInitializer.vendor1.setName("Updated Vendor");
        DatabaseInitializer.vendor1.setVendorId(DatabaseInitializer.vendor2.getVendorId());


        String JSONRequest = this.objectMapper.writeValueAsString(DatabaseInitializer.vendor1);
        ResponsePayload<Vendor> responsePayloadOnUpdate = new ResponsePayload<>(
                null
                ,false
                ,"ERROR: Duplicate entry! Vendor ID already exists!"
        );
        String JSONResponseOnUpdate = this.objectMapper.writeValueAsString(responsePayloadOnUpdate);

        /**
         * Create vendor
         */
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONRequest)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnUpdate));

    }

    @Test
    void deleteVendor() throws Exception {

        /**
         * Before Delete
         */
        ResponsePayload<List<Vendor>> responsePayloadBeforeDelete = new ResponsePayload<>(
                List.of(DatabaseInitializer.vendor1, DatabaseInitializer.vendor2)
                ,true
                ,""
        );
        String JSONResponseBeforeDelete = this.objectMapper.writeValueAsString(responsePayloadBeforeDelete);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseBeforeDelete));


        /**
         * On Delete
         */
        ResponsePayload<List<Vendor>> responsePayloadOnDelete = new ResponsePayload<>(
                null
                ,true
                ,"Vendor has been deleted"
        );
        String JSONResponseOnDelete = this.objectMapper.writeValueAsString(responsePayloadOnDelete);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/vendor/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseOnDelete));


        /**
         * After Delete
         */
        ResponsePayload<List<Vendor>> responsePayloadAfterDelete = new ResponsePayload<>(
                List.of(DatabaseInitializer.vendor2)
                ,true
                ,""
        );
        String JSONResponseAfterDelete = this.objectMapper.writeValueAsString(responsePayloadAfterDelete);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/vendor"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JSONResponseAfterDelete));
    }
}
