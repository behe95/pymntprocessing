package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.*;
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

    private Invoice invoice1;
    private Invoice invoice2;
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

        invoice1 = new Invoice();
        invoice1.setId(1L);
        invoice1.setVendor(vendor1);
        invoice1.setInvoiceStatus(invoiceStatus);
        invoice1.setInvoiceNumber(1);
        invoice1.setInvoiceDescription("Payment Transaction Description 1");
        invoice1.setInvoiceAmount(50.50);


        invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setVendor(vendor1);
        invoice2.setInvoiceStatus(invoiceStatus);
        invoice2.setInvoiceNumber(2);
        invoice2.setInvoiceDescription("Payment Transaction Description 2");
        invoice2.setInvoiceAmount(150.00);
    }

    @Test
    void getAllInvoiceWhenExist() {
        // given
        given(this.invoiceService.getAllInvoice()).willReturn(List.of(invoice1, invoice2));

        // when
        ResponseEntity<ResponseMessage<List<Invoice>>> responseEntity = this.invoiceController.getAllInvoice();

        // then
        ResponseMessage<List<Invoice>> responseMessage = (ResponseMessage<List<Invoice>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<Invoice> invoices = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(2, invoices.size());
        verify(this.invoiceService, times(1)).getAllInvoice();
    }

    @Test
    void getAllInvoiceWhenNotExist() {
        // given
        given(this.invoiceService.getAllInvoice()).willReturn(List.of());

        // when
        ResponseEntity<ResponseMessage<List<Invoice>>> responseEntity = this.invoiceController.getAllInvoice();

        // then
        ResponseMessage<List<Invoice>> responseMessage = (ResponseMessage<List<Invoice>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<Invoice> invoices = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, invoices.size());
        verify(this.invoiceService, times(1)).getAllInvoice();
    }

    @Test
    void getInvoiceByIdWhenFound() {
        // given
        given(this.invoiceService.getInvoiceById(1L)).willReturn(invoice1);

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.getInvoiceById(1L);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice responseInvoice = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(invoice1, responseInvoice);
        verify(this.invoiceService, times(1)).getInvoiceById(1L);
    }

    @Test
    void getInvoiceByIdWhenNotFound() {
        // given
        given(this.invoiceService.getInvoiceById(11L)).willReturn(null);

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.getInvoiceById(11L);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert  responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice responseInvoice = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseInvoice);
        verify(this.invoiceService, times(1)).getInvoiceById(11L);

    }

    @Test
    void getAllInvoiceByVendorIdWhenExist() {
        // given
        given(this.invoiceService.getAllInvoiceByVendorId(1L)).willReturn(List.of(invoice1, invoice2));

        // when
        ResponseEntity<ResponseMessage<List<Invoice>>> responseEntity = this.invoiceController.getAllInvoiceByVendorId(1L);

        // then
        ResponseMessage<List<Invoice>> responseMessage = (ResponseMessage<List<Invoice>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<Invoice> responseInvoices = responseMessage.getData();

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
        ResponseEntity<ResponseMessage<List<Invoice>>> responseEntity = this.invoiceController.getAllInvoiceByVendorId(1L);

        // then
        ResponseMessage<List<Invoice>> responseMessage = (ResponseMessage<List<Invoice>>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        List<Invoice> responseInvoices = responseMessage.getData();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertEquals(0, responseInvoices.size());
        verify(this.invoiceService, times(1)).getAllInvoiceByVendorId(1L);
    }

    @Test
    void createInvoiceWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<Invoice>> responseEntity;
        ResponseMessage<Invoice> responseMessage;
        /**
         * Vendor not provided
         */
        Invoice newInvoice = invoice1;
        newInvoice.setVendor(null);

        // when
        responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(Invoice.class));

        /**
         * Invoice status not provided
         */
        newInvoice.setVendor(vendor1);
        newInvoice.setInvoiceStatus(null);

        // when
        responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(Invoice.class));


    }

    @Test
    void createInvoiceWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<Invoice>> responseEntity;
        ResponseMessage<Invoice> responseMessage;
        /**
         * Invalid vendor provided
         */
        Invoice newInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(Invoice.class));

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
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(1)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).createInvoice(any(Invoice.class));


        reset(vendorService);
        reset(invoiceService);


    }




    @Test
    void createInvoice() {

        Invoice newInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(newInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(newInvoice.getInvoiceStatus().getId())).willReturn(newInvoice.getInvoiceStatus());
        given(this.invoiceService.createInvoice(newInvoice)).willReturn(newInvoice);

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice createdInvoice = responseMessage.getData();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(newInvoice, createdInvoice);
        verify(this.vendorService, times(1)).getVendorById(newInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(newInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).createInvoice(newInvoice);

    }
    @Test
    void createVendorDuplicate() {

        Invoice newInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(newInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(newInvoice.getInvoiceStatus().getId())).willReturn(newInvoice.getInvoiceStatus());
        given(this.invoiceService.createInvoice(newInvoice)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice createdInvoice = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdInvoice);
        verify(this.vendorService, times(1)).getVendorById(newInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(newInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).createInvoice(newInvoice);

    }

    @Test
    void createVendorInternalServerErr() {

        Invoice newInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(newInvoice.getVendor().getId())).willReturn(newInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(newInvoice.getInvoiceStatus().getId())).willReturn(newInvoice.getInvoiceStatus());
        given(this.invoiceService.createInvoice(newInvoice)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.createInvoice(newInvoice);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice createdInvoice = responseMessage.getData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(createdInvoice);
        verify(this.vendorService, times(1)).getVendorById(newInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(newInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).createInvoice(newInvoice);

    }




    @Test
    void updateInvoiceWhenDataNotProvided() {

        ResponseEntity<ResponseMessage<Invoice>> responseEntity;
        ResponseMessage<Invoice> responseMessage;
        /**
         * Vendor not provided
         */
        Invoice updatedInvoice = invoice1;
        updatedInvoice.setVendor(null);

        // when
        responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class),any(Invoice.class));

        /**
         * Invoice status not provided
         */
        updatedInvoice.setVendor(vendor1);
        updatedInvoice.setInvoiceStatus(null);

        // when
        responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class),any(Invoice.class));


    }

    @Test
    void updateInvoiceWhenInvalidDataProvided() {

        ResponseEntity<ResponseMessage<Invoice>> responseEntity;
        ResponseMessage<Invoice> responseMessage;
        /**
         * Invalid vendor provided
         */
        Invoice updatedInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(null);

        // when
        responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(0)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class), any(Invoice.class));

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
        responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        isSuccess = responseMessage.isSuccess();
        responseInvoice = responseMessage.getData();

        assertNull(responseInvoice);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceService, times(1)).getInvoiceStatusById(any(Long.class));
        verify(this.invoiceService, times(0)).updateInvoice(any(Long.class), any(Invoice.class));


        reset(vendorService);
        reset(invoiceService);


    }




    @Test
    void updateInvoice() {

        Invoice updatedInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(updatedInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId())).willReturn(updatedInvoice.getInvoiceStatus());
        given(this.invoiceService.updateInvoice(updatedInvoice.getId(), updatedInvoice)).willReturn(updatedInvoice);

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice responseUpdatedInvoice = responseMessage.getData();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(isSuccess);
        assertEquals(updatedInvoice, responseUpdatedInvoice);
        verify(this.vendorService, times(1)).getVendorById(updatedInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).updateInvoice(updatedInvoice.getId(), updatedInvoice);

    }
    @Test
    void updatedVendorDuplicate() {

        Invoice updatedInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(updatedInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId())).willReturn(updatedInvoice.getInvoiceStatus());
        given(this.invoiceService.updateInvoice(updatedInvoice.getId(), updatedInvoice)).willThrow(new DataIntegrityViolationException(""));

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice responseUpdatedInvoice = responseMessage.getData();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertFalse(isSuccess);
        assertNull(responseUpdatedInvoice);
        verify(this.vendorService, times(1)).getVendorById(updatedInvoice.getVendor().getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId());
        verify(this.invoiceService, times(1)).updateInvoice(updatedInvoice.getId(), updatedInvoice);

    }

    @Test
    void updateVendorInternalServerErr() {

        Invoice updatedInvoice = invoice1;

        // given
        given(this.vendorService.getVendorById(updatedInvoice.getVendor().getId())).willReturn(updatedInvoice.getVendor());
        given(this.invoiceService.getInvoiceStatusById(updatedInvoice.getInvoiceStatus().getId())).willReturn(updatedInvoice.getInvoiceStatus());
        given(this.invoiceService.updateInvoice(updatedInvoice.getId(), updatedInvoice)).willThrow(new RuntimeException(""));

        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.updateInvoice(updatedInvoice.getId(), updatedInvoice);

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        Invoice responseUpdatedInvoice = responseMessage.getData();

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
        given(this.invoiceService.getInvoiceById(invoice1.getId())).willReturn(invoice1);
        given(this.invoiceService.getInvoiceStatusById(invoice1.getInvoiceStatus().getId())).willReturn(invoice1.getInvoiceStatus());


        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.deleteInvoice(invoice1.getId());

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertTrue(isSuccess);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(this.invoiceService, times(1)).getInvoiceById(invoice1.getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(invoice1.getId());
        verify(this.invoiceService, times(1)).deleteInvoice(invoice1.getId());
    }


    @Test
    void deleteInvoiceWhenPaid() {
        InvoiceStatus invoiceStatus1 = new InvoiceStatus(1L, "Paid", 5);
        invoice1.setInvoiceStatus(invoiceStatus1);
        // given
        given(this.invoiceService.getInvoiceById(invoice1.getId())).willReturn(invoice1);
        given(this.invoiceService.getInvoiceStatusById(invoice1.getInvoiceStatus().getId())).willReturn(invoice1.getInvoiceStatus());


        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.deleteInvoice(invoice1.getId());

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertFalse(isSuccess);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(this.invoiceService, times(1)).getInvoiceById(invoice1.getId());
        verify(this.invoiceService, times(1)).getInvoiceStatusById(invoice1.getId());
        verify(this.invoiceService, times(0)).deleteInvoice(invoice1.getId());
    }

    @Test
    void deleteInvoiceWithNonExistingId() {
        // given
        given(this.invoiceService.getInvoiceById(invoice1.getId())).willReturn(null);


        // when
        ResponseEntity<ResponseMessage<Invoice>> responseEntity = this.invoiceController.deleteInvoice(invoice1.getId());

        // then
        ResponseMessage<Invoice> responseMessage = (ResponseMessage<Invoice>) responseEntity.getBody();
        assert responseMessage != null;
        boolean isSuccess = responseMessage.isSuccess();
        assertFalse(isSuccess);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(this.invoiceService, times(1)).getInvoiceById(invoice1.getId());
        verify(this.invoiceService, times(0)).getInvoiceStatusById(invoice1.getId());
        verify(this.invoiceService, times(0)).deleteInvoice(invoice1.getId());
    }
}