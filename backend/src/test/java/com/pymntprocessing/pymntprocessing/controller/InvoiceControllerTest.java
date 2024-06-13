package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.entity.*;
import com.pymntprocessing.pymntprocessing.service.InvoiceService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {


    @Mock
    private InvoiceService invoiceService;

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private InvoiceController invoiceController;

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
    void getAllInvoiceWhenExist() {
        // given
        given(this.invoiceService.getAllInvoice()).willReturn(List.of(invoiceDTO1, invoiceDTO2));

        // when
        ResponseEntity<ResponseMessage<List<InvoiceDTO>>> responseEntity = this.invoiceController.getAllInvoice();

        // then
        ResponseMessage<List<InvoiceDTO>> responseMessage = (ResponseMessage<List<InvoiceDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<InvoiceDTO> invoiceDTOS = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(2, invoiceDTOS.size());
        verify(this.invoiceService, times(1)).getAllInvoice();
    }

    @Test
    void getAllInvoiceWhenNotExist() {
        // given
        given(this.invoiceService.getAllInvoice()).willReturn(List.of());

        // when
        ResponseEntity<ResponseMessage<List<InvoiceDTO>>> responseEntity = this.invoiceController.getAllInvoice();

        // then
        ResponseMessage<List<InvoiceDTO>> responseMessage = (ResponseMessage<List<InvoiceDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<InvoiceDTO> invoiceDTOS = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, invoiceDTOS.size());
        verify(this.invoiceService, times(1)).getAllInvoice();
    }

    @Test
    void getInvoiceByIdWhenFound() {
        // given
        given(this.invoiceService.getInvoiceById(1L)).willReturn(invoiceDTO1);

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.getInvoiceById(1L);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO responseInvoice = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(invoiceDTO1, responseInvoice);
        verify(this.invoiceService, times(1)).getInvoiceById(1L);
    }

    @Test
    void getInvoiceByIdWhenNotFound() {
        // given
        given(this.invoiceService.getInvoiceById(11L)).willReturn(null);

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.getInvoiceById(11L);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert  responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO responseInvoice = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseInvoice);
        verify(this.invoiceService, times(1)).getInvoiceById(11L);

    }

    @Test
    void getAllInvoiceByVendorIdWhenExist() {
        // given
        given(this.invoiceService.getAllInvoiceByVendorId(1L)).willReturn(List.of(invoiceDTO1, invoiceDTO2));

        // when
        ResponseEntity<ResponseMessage<List<InvoiceDTO>>> responseEntity = this.invoiceController.getAllInvoiceByVendorId(1L);

        // then
        ResponseMessage<List<InvoiceDTO>> responseMessage = (ResponseMessage<List<InvoiceDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<InvoiceDTO> responseInvoices = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(2, responseInvoices.size());
        verify(this.invoiceService, times(1)).getAllInvoiceByVendorId(1L);
    }



    @Test
    void getAllInvoiceByVendorIdWhenNotExist() {
        // given
        given(this.invoiceService.getAllInvoiceByVendorId(1L)).willReturn(List.of());

        // when
        ResponseEntity<ResponseMessage<List<InvoiceDTO>>> responseEntity = this.invoiceController.getAllInvoiceByVendorId(1L);

        // then
        ResponseMessage<List<InvoiceDTO>> responseMessage = (ResponseMessage<List<InvoiceDTO>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<InvoiceDTO> responseInvoices = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, responseInvoices.size());
        verify(this.invoiceService, times(1)).getAllInvoiceByVendorId(1L);
    }

    @Test
    void createInvoiceWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity;
        ResponseMessage<InvoiceDTO> responseMessage;
        /**
         * Vendor not provided
         */
        InvoiceDTO newInvoice = invoiceDTO1;
        newInvoice.setVendor(null);

        // when
        responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(InvoiceDTO.class));

        /**
         * InvoiceDTO status not provided
         */
        newInvoice.setVendor(vendor1);
        newInvoice.setInvoiceStatus(null);

        // when
        responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(InvoiceDTO.class));


    }

    @Test
    void createInvoiceWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity;
        ResponseMessage<InvoiceDTO> responseMessage;
        /**
         * Invalid vendor provided
         */
        InvoiceDTO newInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(InvoiceDTO.class));

        reset(vendorService);
        /**
         * Invalid Transaction status provided
         */

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(newInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(newInvoice.getId())).willReturn(null);

        // when
        responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(1)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(InvoiceDTO.class));


        reset(vendorService);
        reset(invoiceService);


    }




    @Test
    void createInvoice() {

        InvoiceDTO newInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(newInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(newInvoice.getInvoiceStatus().getId())).willReturn(newInvoice.getInvoiceStatus());
        given(this.invoiceService.createInvoice(newInvoice)).willReturn(newInvoice);

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO createdInvoice = responseMessage.getData();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(newInvoice, createdInvoice);
        verify(this.vendorService, times(1)).getVendorById(newInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(newInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).createInvoice(newInvoice);

    }
    @Test
    void createVendorDuplicate() {

        InvoiceDTO newInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(newInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(newInvoice.getInvoiceStatus().getId())).willReturn(newInvoice.getInvoiceStatus());
        given(this.invoiceService.createInvoice(newInvoice)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO createdInvoice = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdInvoice);
        verify(this.vendorService, times(1)).getVendorById(newInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(newInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).createInvoice(newInvoice);

    }

    @Test
    void createVendorInternalServerErr() {

        InvoiceDTO newInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(newInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(newInvoice.getInvoiceStatus().getId())).willReturn(newInvoice.getInvoiceStatus());
        given(this.invoiceService.createInvoice(newInvoice)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO createdInvoice = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdInvoice);
        verify(this.vendorService, times(1)).getVendorById(newInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(newInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).createInvoice(newInvoice);

    }




    @Test
    void updateInvoiceWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity;
        ResponseMessage<InvoiceDTO> responseMessage;
        /**
         * Vendor not provided
         */
        InvoiceDTO updatedInvoice = invoiceDTO1;
        updatedInvoice.setVendor(null);

        // when
        responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class),any(InvoiceDTO.class));

        /**
         * InvoiceDTO status not provided
         */
        updatedInvoice.setVendor(vendor1);
        updatedInvoice.setInvoiceStatus(null);

        // when
        responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class),any(InvoiceDTO.class));


    }

    @Test
    void updateInvoiceWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity;
        ResponseMessage<InvoiceDTO> responseMessage;
        /**
         * Invalid vendor provided
         */
        InvoiceDTO updatedInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class), any(InvoiceDTO.class));

        reset(vendorService);
        /**
         * Invalid Transaction status provided
         */

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(updatedInvoice.getVendor());
//        given(this.invoiceService.getInvoiceStatusById(updatedInvoice.getId())).willReturn(null);

        // when
        responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(1)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class), any(InvoiceDTO.class));


        reset(vendorService);
        reset(invoiceService);


    }




    @Test
    void updateInvoice() {

        InvoiceDTO updatedInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(updatedInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId())).willReturn(updatedInvoice.getInvoiceStatus());
        given(this.invoiceService.updateInvoice(updatedInvoice.getId(), updatedInvoice)).willReturn(updatedInvoice);

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO responseUpdatedInvoice = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(updatedInvoice, responseUpdatedInvoice);
        verify(this.vendorService, times(1)).getVendorById(updatedInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).updateInvoice(updatedInvoice.getId(), updatedInvoice);

    }
    @Test
    void updatedVendorDuplicate() {

        InvoiceDTO updatedInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(updatedInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId())).willReturn(updatedInvoice.getInvoiceStatus());
        given(this.invoiceService.updateInvoice(updatedInvoice.getId(), updatedInvoice)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO responseUpdatedInvoice = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseUpdatedInvoice);
        verify(this.vendorService, times(1)).getVendorById(updatedInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).updateInvoice(updatedInvoice.getId(), updatedInvoice);

    }

    @Test
    void updateVendorInternalServerErr() {

        InvoiceDTO updatedInvoice = invoiceDTO1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(updatedInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId())).willReturn(updatedInvoice.getInvoiceStatus());
        given(this.invoiceService.updateInvoice(updatedInvoice.getId(), updatedInvoice)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        InvoiceDTO responseUpdatedInvoice = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseUpdatedInvoice);
        verify(this.vendorService, times(1)).getVendorById(updatedInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).updateInvoice(updatedInvoice.getId(), updatedInvoice);

    }

    @Test
    void deleteInvoice() {
        // given
        given(this.invoiceService.getInvoiceById(invoiceDTO1.getId())).willReturn(invoiceDTO1);
        given(this.invoiceService.getInvoiceStatusById(invoiceDTO1.getInvoiceStatus().getId())).willReturn(invoiceDTO1.getInvoiceStatus());


        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.deleteInvoice(invoiceDTO1.getId());

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertTrue(isSuccess);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(this.invoiceService, times(1)).getInvoiceById(invoiceDTO1.getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(invoiceDTO1.getId());
        verify(this.invoiceService, times(1)).deleteInvoice(invoiceDTO1.getId());
    }


    @Test
    void deleteInvoiceWhenPaid() {
        InvoiceStatus invoiceStatus1 = new InvoiceStatus(1L, "Paid", 5);
        invoiceDTO1.setInvoiceStatus(invoiceStatus1);
        // given
        given(this.invoiceService.getInvoiceById(invoiceDTO1.getId())).willReturn(invoiceDTO1);
        given(this.invoiceService.getInvoiceStatusById(invoiceDTO1.getInvoiceStatus().getId())).willReturn(invoiceDTO1.getInvoiceStatus());


        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.deleteInvoice(invoiceDTO1.getId());

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertFalse(isSuccess);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(this.invoiceService, times(1)).getInvoiceById(invoiceDTO1.getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(invoiceDTO1.getId());
        verify(this.invoiceService, times(0)).deleteInvoice(invoiceDTO1.getId());
    }

    @Test
    void deleteInvoiceWithNonExistingId() {
        // given
        given(this.invoiceService.getInvoiceById(invoiceDTO1.getId())).willReturn(null);


        // when
        ResponseEntity<ResponseMessage<InvoiceDTO>> responseEntity = this.invoiceController.deleteInvoice(invoiceDTO1.getId());

        // then
        ResponseMessage<InvoiceDTO> responseMessage = (ResponseMessage<InvoiceDTO>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertFalse(isSuccess);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(this.invoiceService, times(1)).getInvoiceById(invoiceDTO1.getId());
        verify(this.invoiceService, times(0)).getInvoiceStatusById(invoiceDTO1.getId());
        verify(this.invoiceService, times(0)).deleteInvoice(invoiceDTO1.getId());
    }
}