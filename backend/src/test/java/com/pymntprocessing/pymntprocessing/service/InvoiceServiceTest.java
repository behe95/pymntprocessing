package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;
import com.pymntprocessing.pymntprocessing.constant.db.InvoiceStatusValue;
import com.pymntprocessing.pymntprocessing.exception.GlobalErrorException;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.exception.InvoiceNotFoundException;
import com.pymntprocessing.pymntprocessing.exception.VendorNotFoundException;
import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Invoice;
import com.pymntprocessing.pymntprocessing.model.entity.InvoiceStatus;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.model.mapper.InvoiceMapper;
import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
import com.pymntprocessing.pymntprocessing.repository.InvoiceRepository;
import com.pymntprocessing.pymntprocessing.repository.InvoiceStatusRepository;
import com.pymntprocessing.pymntprocessing.repository.VendorRepository;
import com.pymntprocessing.pymntprocessing.service.impl.InvoiceServiceImpl;
import com.pymntprocessing.pymntprocessing.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {



    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private VendorRepository vendorRepository;
    @Mock
    private InvoiceStatusRepository invoiceStatusRepository;
    @Mock
    private VendorServiceImpl vendorService;

    @Mock
    private InvoiceMapper invoiceMapperMock;
    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;

    private Invoice invoice1;
    private InvoiceDTO invoiceDTO1;
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
        invoice1.setPaymentTransaction(List.of());
        invoice1.setInvoiceAmount(0.0);



        invoiceDTO1 = new InvoiceDTO();
        invoiceDTO1.setId(1L);
        invoiceDTO1.setVendor(vendor1);
        invoiceDTO1.setInvoiceStatus(invoiceStatus);
        invoiceDTO1.setInvoiceNumber(1);
        invoiceDTO1.setInvoiceDescription("Payment Transaction Description 1");
        invoiceDTO1.setPaymentTransactionDTO(List.of());
        invoiceDTO1.setInvoiceAmount(0.0);


        invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setVendor(vendor1);
        invoice2.setInvoiceStatus(invoiceStatus);
        invoice2.setInvoiceNumber(2);
        invoice2.setInvoiceDescription("Payment Transaction Description 2");
        invoice2.setPaymentTransaction(List.of());
        invoice2.setInvoiceAmount(0.0);
    }

    @Test
    void getInvoiceByIdWhenExist() {

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.of(invoice1));
        given(this.invoiceMapperMock.convertToDTO(any(Invoice.class))).willReturn(invoiceDTO1);

        // when
        InvoiceDTO invoiceDTO = this.invoiceService.getInvoiceById(1L);

        // then
        assertEquals(1L, invoiceDTO.getId());
        assertEquals(invoice1.getInvoiceDescription(), invoiceDTO.getInvoiceDescription());
        assertEquals(invoice1.getInvoiceNumber(), invoiceDTO.getInvoiceNumber());
        assertEquals(invoice1.getInvoiceDate(), invoiceDTO.getInvoiceDate());
        assertEquals(invoice1.getInvoiceDueDate(), invoiceDTO.getInvoiceDueDate());
        assertEquals(invoice1.getInvoiceReceivedDate(), invoiceDTO.getInvoiceReceivedDate());
        assertEquals(invoice1.getPaymentTransaction().size(), invoiceDTO.getPaymentTransactionDTO().size());
        assertEquals(invoice1.getInvoiceAmount(), invoiceDTO.getInvoiceAmount());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(1)).convertToDTO(any(Invoice.class));
    }


    @Test
    void getInvoiceByIdWhenNotExist() {

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException = assertThrows(InvoiceNotFoundException.class, () -> this.invoiceService.getInvoiceById(1L));

        // then
        assertEquals("Invoice doesn't exist!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(0)).convertToDTO(any(Invoice.class));
    }


    @Test
    void createInvoiceWhenInvalidVendor() {

        invoiceDTO1.setId(null);

        // given
        given(this.vendorService.getVendorById(any(Long.class))).willThrow(new VendorNotFoundException());


        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.invoiceService.createInvoice(invoiceDTO1));

        // then
        assertEquals("Invalid vendor provided! Vendor doesn't exist!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(0)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(0)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(0)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(0)).convertToDTO(any(Invoice.class));

    }

    @Test
    void createInvoiceWhenInvalidInvoiceStatusProvided() {

        invoiceDTO1.setId(null);

        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.invoiceStatusRepository.findById(any(Long.class))).willReturn(Optional.empty());


        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.invoiceService.createInvoice(invoiceDTO1));

        // then
        assertEquals("Invalid invoice status provided!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(0)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(0)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(0)).convertToDTO(any(Invoice.class));

    }


    @Test
    void createInvoice() {

        invoiceDTO1.setId(null);

        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.invoiceStatusRepository.findById(any(Long.class))).willReturn(Optional.of(invoiceStatus));
        given(this.invoiceMapperMock.convertToEntity(any(InvoiceDTO.class))).willReturn(invoice1);
        given(this.invoiceRepository.save(any(Invoice.class))).willReturn(invoice1);

        invoiceDTO1.setId(1L);
        given(this.invoiceMapperMock.convertToDTO(any(Invoice.class))).willReturn(invoiceDTO1);


        // when
        InvoiceDTO invoiceDTO = this.invoiceService.createInvoice(invoiceDTO1);

        // then

        assertEquals(1L, invoiceDTO.getId());
        assertEquals(invoice1.getInvoiceDescription(), invoiceDTO.getInvoiceDescription());
        assertEquals(invoice1.getInvoiceNumber(), invoiceDTO.getInvoiceNumber());
        assertEquals(invoice1.getInvoiceDate(), invoiceDTO.getInvoiceDate());
        assertEquals(invoice1.getInvoiceDueDate(), invoiceDTO.getInvoiceDueDate());
        assertEquals(invoice1.getInvoiceReceivedDate(), invoiceDTO.getInvoiceReceivedDate());
        assertEquals(invoice1.getPaymentTransaction().size(), invoiceDTO.getPaymentTransactionDTO().size());
        assertEquals(invoice1.getInvoiceAmount(), invoiceDTO.getInvoiceAmount());

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(1)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(1)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(1)).convertToDTO(any(Invoice.class));

    }


    @Test
    void updateInvoiceWhenInvalidInvoice() {

        invoiceDTO1.setId(1L);

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.empty());


        // when
        GlobalErrorException globalErrorException = assertThrows(InvoiceNotFoundException.class, () -> this.invoiceService.updateInvoice(1L, invoiceDTO1));

        // then
        assertEquals("Invoice doesn't exist!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.vendorService, times(0)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(0)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(0)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(0)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(0)).convertToDTO(any(Invoice.class));

    }


    @Test
    void updateInvoiceWhenInvalidVendor() {

        invoiceDTO1.setId(1L);

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.of(invoice1));
        given(this.vendorService.getVendorById(any(Long.class))).willThrow(new VendorNotFoundException());


        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.invoiceService.updateInvoice(1L, invoiceDTO1));

        // then
        assertEquals("Invalid vendor provided! Vendor doesn't exist!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(0)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(0)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(0)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(0)).convertToDTO(any(Invoice.class));

    }


    @Test
    void updateInvoiceWhenInvalidInvoiceStatus() {

        invoiceDTO1.setId(1L);
        invoiceDTO1.setInvoiceStatus(invoiceStatus);

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.of(invoice1));
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.invoiceStatusRepository.findById(any(Long.class))).willReturn(Optional.empty());


        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.invoiceService.updateInvoice(1L, invoiceDTO1));

        // then
        assertEquals("Invalid transaction status provided!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(0)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(0)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(0)).convertToDTO(any(Invoice.class));

    }


    @Test
    void updateInvoiceWhenInvoiceStatusNotOpenOrCommitted() {

        invoiceDTO1.setId(1L);
        invoiceStatus.setName(InvoiceStatusValue.PAID.toString());
        invoiceDTO1.setInvoiceStatus(invoiceStatus);

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.of(invoice1));
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.invoiceStatusRepository.findById(any(Long.class))).willReturn(Optional.of(invoiceStatus));


        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.invoiceService.updateInvoice(1L, invoiceDTO1));

        // then
        assertEquals("Unable to update transaction. Transaction status: " + invoiceDTO1.getInvoiceStatus().getName(), globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(0)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(0)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(0)).convertToDTO(any(Invoice.class));

    }


    @Test
    void updateInvoice() {
        invoiceDTO1.setId(1L);
        invoiceDTO1.setId(1L);
        invoiceStatus.setName(InvoiceStatusValue.OPEN.toString());
        invoiceDTO1.setInvoiceStatus(invoiceStatus);

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.of(invoice1));
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.invoiceStatusRepository.findById(any(Long.class))).willReturn(Optional.of(invoiceStatus));
        given(this.invoiceMapperMock.convertToEntity(any(InvoiceDTO.class))).willReturn(invoice1);
        given(this.invoiceRepository.save(any(Invoice.class))).willReturn(invoice1);
        given(this.invoiceMapperMock.convertToDTO(any(Invoice.class))).willReturn(invoiceDTO1);


        // when
        InvoiceDTO invoiceDTO = this.invoiceService.updateInvoice(1L, invoiceDTO1);

        // then

        assertEquals(1L, invoiceDTO.getId());
        assertEquals(invoice1.getInvoiceDescription(), invoiceDTO.getInvoiceDescription());
        assertEquals(invoice1.getInvoiceNumber(), invoiceDTO.getInvoiceNumber());
        assertEquals(invoice1.getInvoiceDate(), invoiceDTO.getInvoiceDate());
        assertEquals(invoice1.getInvoiceDueDate(), invoiceDTO.getInvoiceDueDate());
        assertEquals(invoice1.getInvoiceReceivedDate(), invoiceDTO.getInvoiceReceivedDate());
        assertEquals(invoice1.getPaymentTransaction().size(), invoiceDTO.getPaymentTransactionDTO().size());
        assertEquals(invoice1.getInvoiceAmount(), invoiceDTO.getInvoiceAmount());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.invoiceStatusRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(1)).convertToEntity(any(InvoiceDTO.class));
        verify(this.invoiceRepository, times(1)).save(any(Invoice.class));
        verify(this.invoiceMapperMock, times(1)).convertToDTO(any(Invoice.class));

    }



    @Test
    void deleteInvoiceWhenNotExist() {

        invoiceDTO1.setId(1L);

        // given
        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.empty());


        // when
        GlobalErrorException globalErrorException = assertThrows(InvoiceNotFoundException.class, () -> this.invoiceService.deleteInvoice(1L));

        // then
        assertEquals("Invoice doesn't exist!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceRepository, times(0)).deleteById(any(Long.class));

    }


    @Test
    void deleteInvoiceWhenPaid() {

        invoice1.setId(1L);
        invoiceDTO1.setId(1L);
        invoiceStatus.setName(InvoiceStatusValue.PAID.toString());
        invoice1.setInvoiceStatus(invoiceStatus);
        invoiceDTO1.setInvoiceStatus(invoiceStatus);

        // given

        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.of(invoice1));
        given(this.invoiceMapperMock.convertToDTO(any(Invoice.class))).willReturn(invoiceDTO1);


        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.invoiceService.deleteInvoice(1L));

        // then
        assertEquals("Unable to delete transaction. Transaction status: " + invoiceStatus.getName(), globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(1)).convertToDTO(any(Invoice.class));
        verify(this.invoiceRepository, times(0)).deleteById(any(Long.class));

    }


    @Test
    void deleteInvoice() {

        invoice1.setId(1L);
        invoiceDTO1.setId(1L);
        invoiceStatus.setName(InvoiceStatusValue.OPEN.toString());
        invoice1.setInvoiceStatus(invoiceStatus);
        invoiceDTO1.setInvoiceStatus(invoiceStatus);

        // given

        given(this.invoiceRepository.findById(any(Long.class))).willReturn(Optional.of(invoice1));
        given(this.invoiceMapperMock.convertToDTO(any(Invoice.class))).willReturn(invoiceDTO1);


        // when
        this.invoiceService.deleteInvoice(1L);

        // then
        verify(this.invoiceRepository, times(1)).findById(any(Long.class));
        verify(this.invoiceMapperMock, times(1)).convertToDTO(any(Invoice.class));
        verify(this.invoiceRepository, times(1)).deleteById(any(Long.class));

    }
}