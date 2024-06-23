package com.pymntprocessing.pymntprocessing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.exception.InvoiceNotFoundException;
import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.entity.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.service.InvoiceService;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc
class InvoiceControllerTest {


    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private VendorService vendorService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private InvoiceDTO invoiceDTO1;
    private InvoiceDTO invoiceDTO2;
    InvoiceStatus invoiceStatus;

    Vendor vendor1;

    @BeforeEach
    void setUp() {
        invoiceStatus = new InvoiceStatus(1L, "Open", 1);

        vendor1 = new Vendor();
        vendor1.setId(1L);
        vendor1.setName("Vendor One");
        vendor1.setAddress("11 Long Ave, NYC");
        vendor1.setVendorId("EVID0001");

        invoiceDTO1 = new InvoiceDTO();
        invoiceDTO1.setId(1L);
        invoiceDTO1.setVendor(vendor1);
        invoiceDTO1.setInvoiceStatus(invoiceStatus);
        invoiceDTO1.setInvoiceNumber(1);
        invoiceDTO1.setInvoiceDescription("Payment Transaction Description 1");
        invoiceDTO1.setInvoiceAmount(50.50);


        invoiceDTO2 = new InvoiceDTO();
        invoiceDTO2.setId(2L);
        invoiceDTO2.setVendor(vendor1);
        invoiceDTO2.setInvoiceStatus(invoiceStatus);
        invoiceDTO2.setInvoiceNumber(2);
        invoiceDTO2.setInvoiceDescription("Payment Transaction Description 2");
        invoiceDTO2.setInvoiceAmount(150.00);
    }

    @Test
    void getAllInvoiceWhenExist() throws Exception {
        // given
        given(this.invoiceService.getAllInvoice()).willReturn(List.of(invoiceDTO1,invoiceDTO2));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(List.of(invoiceDTO1,invoiceDTO2), true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).getAllInvoice();
    }

    @Test
    void getAllInvoiceWhenNotExist() throws Exception {
        // given
        given(this.invoiceService.getAllInvoice()).willReturn(List.of());

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(List.of(), true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).getAllInvoice();
    }

    @Test
    void getInvoiceByIdWhenFound() throws Exception {
        invoiceDTO1.setId(1L);
        // given
        given(this.invoiceService.getInvoiceById(any(Long.class))).willReturn(invoiceDTO1);

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(invoiceDTO1, true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).getInvoiceById(any(Long.class));
    }

    @Test
    void getInvoiceByIdWhenNotFound() throws Exception {
        invoiceDTO1.setId(1L);
        // given
        given(this.invoiceService.getInvoiceById(any(Long.class))).willThrow(new InvoiceNotFoundException());

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvoiceNotFoundException(), false, "Invoice doesn't exist!"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).getInvoiceById(any(Long.class));

    }

    @Test
    void getAllInvoiceByVendorIdWhenExist() throws Exception {
        // given
        given(this.invoiceService.getAllInvoiceByVendorId(any(Long.class))).willReturn(List.of(invoiceDTO1,invoiceDTO2));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(List.of(invoiceDTO1,invoiceDTO2), true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/invoice/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).getAllInvoiceByVendorId(any(Long.class));
    }



    @Test
    void getAllInvoiceByVendorIdWhenNotExist() throws Exception {
        // given
        given(this.invoiceService.getAllInvoiceByVendorId(any(Long.class))).willReturn(List.of());

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(List.of(), true, ""));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/invoice/vendor/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).getAllInvoiceByVendorId(any(Long.class));
    }

    @Test
    void createInvoiceWhenVendorDataNotProvided() throws Exception {
        /**
         * Vendor not provided
         */
        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setVendor(null);

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);

        String errorMessage = "Vendor information not provided!";

        // given
        given(this.invoiceService.createInvoice(any(InvoiceDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(0)).createInvoice(any(InvoiceDTO.class));


    }


    @Test
    void createInvoiceWhenInvoiceStatusDataNotProvided() throws Exception {

        /**
         * InvoiceDTO status not provided
         */
        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setInvoiceStatus(null);

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);

        String errorMessage = "Invoice status not provided!";
        // given
        given(this.invoiceService.createInvoice(any(InvoiceDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(0)).createInvoice(any(InvoiceDTO.class));


    }
    @Test
    void createInvoiceWhenInvalidVendorDataProvided() throws Exception {
        /**
         * Vendor not provided
         */
        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setVendor(vendor1);

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);

        String errorMessage = "Invalid vendor provided!";

        // given
        given(this.invoiceService.createInvoice(any(InvoiceDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).createInvoice(any(InvoiceDTO.class));


    }


    @Test
    void createInvoiceWhenInvalidInvoiceStatusDataProvided() throws Exception {

        /**
         * InvoiceDTO status not provided
         */
        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setInvoiceStatus(invoiceStatus);

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);

        String errorMessage = "Invalid status provided!";
        // given
        given(this.invoiceService.createInvoice(any(InvoiceDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).createInvoice(any(InvoiceDTO.class));


    }

    @Test
    void createInvoice() throws Exception {

        InvoiceDTO newInvoice = invoiceDTO1;

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);
        // given
        given(this.invoiceService.createInvoice(any(InvoiceDTO.class))).willReturn(newInvoice);

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(newInvoice, true, "Invoice created!"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).createInvoice(any(InvoiceDTO.class));

    }
    @Test
    void createInvoiceDuplicate() throws Exception {

        InvoiceDTO newInvoice = invoiceDTO1;

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);
        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "ERROR: Duplicate entry!"));

        // given
        given(this.invoiceService.createInvoice(any(InvoiceDTO.class))).willThrow(new DataIntegrityViolationException(""));


        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).createInvoice(any(InvoiceDTO.class));

    }

    @Test
    void createInvoiceInternalServerErr() throws Exception {

        InvoiceDTO newInvoice = invoiceDTO1;


        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "Something went wrong! Additional Error Message"));

        // given
        given(this.invoiceService.createInvoice(any(InvoiceDTO.class))).willThrow(new RuntimeException("Additional Error Message"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/invoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).createInvoice(any(InvoiceDTO.class));

    }


    @Test
    void updateInvoiceWhenVendorDataNotProvided() throws Exception {
        /**
         * Vendor not provided
         */
        InvoiceDTO updatedInvoice = invoiceDTO1;
        updatedInvoice.setId(1L);
        updatedInvoice.setVendor(null);

        String requestJSON = this.objectMapper.writeValueAsString(updatedInvoice);

        String errorMessage = "Vendor information not provided!";

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(0)).updateInvoice(any(Long.class), any(InvoiceDTO.class));


    }


    @Test
    void updateInvoiceWhenInvoiceStatusDataNotProvided() throws Exception {

        /**
         * InvoiceDTO status not provided
         */
        InvoiceDTO updatedInvoice = invoiceDTO1;
        updatedInvoice.setId(1L);
        updatedInvoice.setInvoiceStatus(null);

        String requestJSON = this.objectMapper.writeValueAsString(updatedInvoice);

        String errorMessage = "Invoice status not provided!";

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(0)).updateInvoice(any(Long.class), any(InvoiceDTO.class));


    }


    @Test
    void updateInvoiceWhenInvoiceIdNotExist() throws Exception {
        /**
         * Vendor not provided
         */
        InvoiceDTO updatedInvoice = invoiceDTO1;
        updatedInvoice.setId(1L);
        updatedInvoice.setVendor(vendor1);

        String requestJSON = this.objectMapper.writeValueAsString(updatedInvoice);

        String errorMessage = "Invoice doesn't exist!";

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvoiceNotFoundException(), false, errorMessage));


        // given
        given(this.invoiceService.updateInvoice(any(Long.class), any(InvoiceDTO.class))).willThrow(new InvoiceNotFoundException());

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).updateInvoice(any(Long.class), any(InvoiceDTO.class));


    }

    @Test
    void updateInvoiceWhenInvalidVendorDataProvided() throws Exception {
        /**
         * Vendor not provided
         */
        InvoiceDTO updatedInvoice = invoiceDTO1;
        updatedInvoice.setId(1L);
        updatedInvoice.setVendor(vendor1);

        String requestJSON = this.objectMapper.writeValueAsString(updatedInvoice);

        String errorMessage = "Invalid vendor provided!";

        // given
        given(this.invoiceService.updateInvoice(any(Long.class), any(InvoiceDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).updateInvoice(any(Long.class), any(InvoiceDTO.class));


    }


    @Test
    void updateInvoiceWhenInvalidInvoiceStatusDataProvided() throws Exception {

        /**
         * InvoiceDTO status not provided
         */
        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setInvoiceStatus(invoiceStatus);

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);

        String errorMessage = "Invalid transaction status provided!";
        // given
        given(this.invoiceService.updateInvoice(any(Long.class), any(InvoiceDTO.class))).willThrow(new InvalidDataProvidedException(errorMessage));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).updateInvoice(any(Long.class), any(InvoiceDTO.class));


    }



    @Test
    void updateInvoice() throws Exception {

        InvoiceDTO newInvoice = invoiceDTO1;

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);
        // given
        given(this.invoiceService.updateInvoice(any(Long.class), any(InvoiceDTO.class))).willReturn(newInvoice);

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(newInvoice, true, "Invoice has been updated!"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).updateInvoice(any(Long.class), any(InvoiceDTO.class));

    }
    @Test
    void updateInvoiceDuplicate() throws Exception {

        InvoiceDTO updatedInvoice = invoiceDTO1;

        String requestJSON = this.objectMapper.writeValueAsString(updatedInvoice);
        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "ERROR: Duplicate entry!"));

        // given
        given(this.invoiceService.updateInvoice(any(Long.class), any(InvoiceDTO.class))).willThrow(new DataIntegrityViolationException(""));


        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).updateInvoice(any(Long.class), any(InvoiceDTO.class));

    }

    @Test
    void updateInvoiceInternalServerErr() throws Exception {

        InvoiceDTO updatedInvoice = invoiceDTO1;


        String requestJSON = this.objectMapper.writeValueAsString(updatedInvoice);


        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, false, "Something went wrong! Additional Error Message"));

        // given
        given(this.invoiceService.updateInvoice(any(Long.class), any(InvoiceDTO.class))).willThrow(new RuntimeException("Additional Error Message"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJSON)
                ).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).updateInvoice(any(Long.class), any(InvoiceDTO.class));

    }


    @Test
    void deleteInvoice() throws Exception {

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(null, true, "Invoice has been deleted!"));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).deleteInvoice(any(Long.class));
    }


    @Test
    void deleteInvoiceWhenPaid() throws Exception {

        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setInvoiceStatus(invoiceStatus);

        String requestJSON = this.objectMapper.writeValueAsString(newInvoice);

        String errorMessage = "Unable to delete transaction. Transaction status: " + invoiceStatus.getName();
        // given
        willThrow(new InvalidDataProvidedException(errorMessage)).given(this.invoiceService).deleteInvoice(any(Long.class));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvalidDataProvidedException(errorMessage), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).deleteInvoice(any(Long.class));



    }

    @Test
    void deleteInvoiceWithNonExistingId() throws Exception {

        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setInvoiceStatus(invoiceStatus);

        String errorMessage = "Invoice doesn't exist!";
        // given
        willThrow(new InvoiceNotFoundException()).given(this.invoiceService).deleteInvoice(any(Long.class));

        String expectedReturnJSON = this.objectMapper
                .writeValueAsString(new ResponsePayload<>(new InvoiceNotFoundException(), false, errorMessage));

        // when
        // then
        this.mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/invoice/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedReturnJSON));

        then(this.invoiceService).should(times(1)).deleteInvoice(any(Long.class));
    }
}