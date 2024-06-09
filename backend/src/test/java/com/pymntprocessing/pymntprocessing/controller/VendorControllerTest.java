package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.ResponseMessage;
import com.pymntprocessing.pymntprocessing.model.Vendor;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class VendorControllerTest {

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private  VendorController vendorController;

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
    void getVednorByIdWhenFound() {
        // given
        given(this.vendorService.getVendorById(1L)).willReturn(vendor1);

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.getVednorById(1L);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor responseVednor = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(vendor1, responseVednor);

    }

    @Test
    void getVednorByIdWhenNotFound() {
        // given
        given(this.vendorService.getVendorById(11L)).willReturn(null);

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.getVednorById(11L);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor responseVednor = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseVednor);

    }
}