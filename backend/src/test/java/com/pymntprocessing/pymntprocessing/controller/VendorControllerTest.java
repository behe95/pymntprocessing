package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.entity.ResponseMessage;
import com.pymntprocessing.pymntprocessing.entity.Vendor;
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
    void getVendorByIdWhenFound() {
        // given
        given(this.vendorService.getVendorById(1L)).willReturn(vendor1);

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.getVendorById(1L);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor responseVendor = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(vendor1, responseVendor);
        verify(this.vendorService, times(1)).getVendorById(1L);

    }

    @Test
    void getVendorByIdWhenNotFound() {
        // given
        given(this.vendorService.getVendorById(11L)).willReturn(null);

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.getVendorById(11L);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor responseVendor = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseVendor);
        verify(this.vendorService, times(1)).getVendorById(11L);

    }

    @Test
    void getAllVendorsWhenExist() {
        // given
        given(this.vendorService.getAllVendors()).willReturn(List.of(vendor1, vendor2));

        // when
        ResponseEntity<ResponseMessage<List<Vendor>>> responseEntity = this.vendorController.getAllVendors();

        // then
        ResponseMessage<List<Vendor>> responseMessage = (ResponseMessage<List<Vendor>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<Vendor> vendors = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(2, vendors.size());
        verify(this.vendorService, times(1)).getAllVendors();
    }
    @Test
    void getAllVendorsWhenNotExist() {
        // given
        given(this.vendorService.getAllVendors()).willReturn(List.of());

        // when
        ResponseEntity<ResponseMessage<List<Vendor>>> responseEntity = this.vendorController.getAllVendors();

        // then
        ResponseMessage<List<Vendor>> responseMessage = (ResponseMessage<List<Vendor>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<Vendor> vendors = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, vendors.size());
        verify(this.vendorService, times(1)).getAllVendors();
    }




    @Test
    void createVendor() {

        Vendor vendor = new Vendor();
        vendor.setId(3L);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0003");

        // given
        given(this.vendorService.createVendor(vendor)).willReturn(vendor);

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.createVendor(vendor);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor createdVendor = responseMessage.getData();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(vendor, createdVendor);
        verify(this.vendorService, times(1)).createVendor(vendor);

    }
    @Test
    void createVendorDuplicate() {

        Vendor vendor = new Vendor();
        vendor.setId(2L);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0002");

        // given
        given(this.vendorService.createVendor(vendor)).willThrow(new DataIntegrityViolationException("Duplicate entry "+ vendor.getVendorId()+" for key 'vendor.vendorId'"));

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.createVendor(vendor);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor createdVendor = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdVendor);
        verify(this.vendorService, times(1)).createVendor(vendor);

    }

    @Test
    void createVendorInternalServerErr() {

        Vendor vendor = new Vendor();
        vendor.setId(2L);
        vendor.setName("New Vendor");
        vendor.setAddress("121 Demo Ave, LA");
        vendor.setVendorId("EVID0002");

        // given
        given(this.vendorService.createVendor(any(Vendor.class))).willThrow(new RuntimeException("Something went wrong!"));
        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.createVendor(vendor);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor createdVendor = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdVendor);
        verify(this.vendorService, times(1)).createVendor(vendor);

    }


    @Test
    void updateVendor() {

        vendor1.setName("Vendor One Updated");

        // given
        given(this.vendorService.updateVendor(vendor1.getId(), vendor1)).willReturn(vendor1);

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.updateVendor(vendor1.getId(), vendor1);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor updatedVendor = responseMessage.getData();


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(vendor1, updatedVendor);
        assertEquals(vendor1.getName(), updatedVendor.getName());
        verify(this.vendorService, times(1)).updateVendor(vendor1.getId(), vendor1);

    }
    @Test
    void updateVendorDuplicate() {

        vendor1.setName("Vendor One Update");
        vendor1.setVendorId("EVID0002");

        // given
        given(this.vendorService.updateVendor(vendor1.getId(),vendor1)).willThrow(new DataIntegrityViolationException("Duplicate entry "+ vendor1.getVendorId()+" for key 'vendor.vendorId'"));

        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.updateVendor(vendor1.getId(),vendor1);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor updatedVendor = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(updatedVendor);
        verify(this.vendorService, times(1)).updateVendor(vendor1.getId(), vendor1);

    }

    @Test
    void updateVendorInternalServerErr() {

        vendor1.setName("Vendor One Updated");

        // given
        given(this.vendorService.updateVendor(any(Long.class), any(Vendor.class))).willThrow(new RuntimeException("Something went wrong!"));
        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.updateVendor(vendor1.getId(), vendor1);

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Vendor updatedVendor = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(updatedVendor);
        verify(this.vendorService, times(1)).updateVendor(vendor1.getId(), vendor1);

    }

    @Test
    void deleteVendor() {
        // given
        given(this.vendorService.getVendorById(vendor1.getId())).willReturn(vendor1);


        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.deleteVendor(vendor1.getId());

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertTrue(isSuccess);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteVendorWithNonExistingId() {
        // given
        given(this.vendorService.getVendorById(vendor1.getId())).willReturn(null);


        // when
        ResponseEntity<ResponseMessage<Vendor>> responseEntity = this.vendorController.deleteVendor(vendor1.getId());

        // then
        ResponseMessage<Vendor> responseMessage = (ResponseMessage<Vendor>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertFalse(isSuccess);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}