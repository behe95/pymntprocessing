package com.pymntprocessing.pymntprocessing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pymntprocessing.pymntprocessing.exception.VendorNotFoundException;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VendorController.class)
@AutoConfigureMockMvc
class VendorControllerTest {

    @MockBean
    private VendorService vendorService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Vendor vendor1;
    private Vendor vendor2;


    @BeforeEach
    public void setup() {
        vendor1 = new Vendor();
        vendor1.setId(1L);
        vendor1.setName("Vendor One");
        vendor1.setAddress("11 Long Ave, NYC");
        vendor1.setVendorId("EVID0001");

        vendor2 = new Vendor();
        vendor2.setId(2L);
        vendor2.setName("Vendor Two");
        vendor2.setAddress("11 Main Ave, CA");
        vendor2.setVendorId("EVID0002");
    }

    @Test
    void getVendorByIdWhenFound() throws Exception {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(vendor1, true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).getVendorById(any(Long.class));

    }

    @Test
    void getVendorByIdWhenNotFound() throws Exception {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willThrow(new VendorNotFoundException());

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new VendorNotFoundException(), false, "Vendor doesn't exist!"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).getVendorById(any(Long.class));

    }

    @Test
    void getAllVendorsWhenExist() throws Exception {
        // given
        given(this.vendorService.getAllVendors()).willReturn(List.of(vendor1,vendor2));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(List.of(vendor1,vendor2), true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/vendor")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).getAllVendors();
    }
    @Test
    void getAllVendorsWhenNotExist() throws Exception {
        // given
        given(this.vendorService.getAllVendors()).willReturn(List.of());

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(List.of(), true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/vendor")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).getAllVendors();
    }




    @Test
    void createVendor() throws Exception {

        Vendor vendor = new Vendor();
        vendor.setId(null);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0003");

        Vendor createdVendor = new Vendor();
        createdVendor.setId(1L);
        createdVendor.setName("New Vendor");
        createdVendor.setAddress("121 Demo Ave, LA");
        createdVendor.setVendorId("EVID0003");

        // given
        given(this.vendorService.createVendor(any(Vendor.class))).willReturn(createdVendor);

        String requestJSON = this.objectMapper.writeValueAsString(vendor);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(createdVendor, true, "Vendor " + createdVendor.getName() + " created successfully!"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/vendor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).createVendor(any(Vendor.class));

    }
    @Test
    void createVendorDuplicate() throws Exception {

        Vendor vendor = new Vendor();
        vendor.setId(null);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0003");

        String requestJSON = this.objectMapper.writeValueAsString(vendor);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "ERROR: Duplicate entry! Duplicate entry "+ vendor.getVendorId()+" for key 'vendor.vendorId'"));

        // given
        given(this.vendorService.createVendor(any(Vendor.class))).willThrow(new DataIntegrityViolationException("Duplicate entry "+ vendor.getVendorId()+" for key 'vendor.vendorId'"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/vendor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).createVendor(any(Vendor.class));

    }

    @Test
    void createVendorInternalServerErr() throws Exception {

        Vendor vendor = new Vendor();
        vendor.setId(null);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0003");

        String requestJSON = this.objectMapper.writeValueAsString(vendor);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "Something went wrong! Additional Error Message"));

        // given
        given(this.vendorService.createVendor(any(Vendor.class))).willThrow(new RuntimeException("Additional Error Message"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/vendor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).createVendor(any(Vendor.class));

    }


    @Test
    void updateVendor() throws Exception {

        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0003");

        Vendor updatedVendor = new Vendor();
        updatedVendor.setId(1L);
        updatedVendor.setName("New Vendor");
        updatedVendor.setAddress("121 Demo Ave, LA");
        updatedVendor.setVendorId("EVID0003");

        // given
        given(this.vendorService.updateVendor(any(Long.class), any(Vendor.class))).willReturn(updatedVendor);

        String requestJSON = this.objectMapper.writeValueAsString(vendor);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(updatedVendor, true, "Vendor " + updatedVendor.getName() + " has been updated!"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).updateVendor(any(Long.class), any(Vendor.class));

    }
    @Test
    void updateVendorDuplicate() throws Exception {

        Vendor vendor = new Vendor();
        vendor.setId(null);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0003");

        String requestJSON = this.objectMapper.writeValueAsString(vendor);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "ERROR: Duplicate entry! Duplicate entry "+ vendor.getVendorId()+" for key 'vendor.vendorId'"));

        // given
        given(this.vendorService.updateVendor(any(Long.class), any(Vendor.class))).willThrow(new DataIntegrityViolationException("Duplicate entry "+ vendor.getVendorId()+" for key 'vendor.vendorId'"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).updateVendor(any(Long.class), any(Vendor.class));

    }

    @Test
    void updateVendorInternalServerErr() throws Exception {

        Vendor vendor = new Vendor();
        vendor.setId(null);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0003");

        String requestJSON = this.objectMapper.writeValueAsString(vendor);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "Something went wrong! Additional Error Message"));

        // given
        given(this.vendorService.updateVendor(any(Long.class), any(Vendor.class))).willThrow(new RuntimeException("Additional Error Message"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).updateVendor(any(Long.class), any(Vendor.class));

    }

    @Test
    void deleteVendor() throws Exception {

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, true, "Vendor has been deleted"));
        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).deleteVendor(any(Long.class));
    }

    @Test
    void deleteVendorWithNonExistingId() throws Exception {
        // given
        willThrow(new VendorNotFoundException()).given(this.vendorService).deleteVendor(any(Long.class));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new VendorNotFoundException(), false, "Vendor doesn't exist!"));
        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.vendorService).should(times(1)).deleteVendor(any(Long.class));
    }
}