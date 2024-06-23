package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;
import com.pymntprocessing.pymntprocessing.controller.PaymentTransactionController;
import com.pymntprocessing.pymntprocessing.exception.*;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
import com.pymntprocessing.pymntprocessing.model.mapper.ProductMapper;
import com.pymntprocessing.pymntprocessing.repository.PaymentTransactionRepository;
import com.pymntprocessing.pymntprocessing.repository.TransactionTypeRepository;
import com.pymntprocessing.pymntprocessing.service.impl.PaymentTransactionServiceImpl;
import com.pymntprocessing.pymntprocessing.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionServiceTest {



    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private PaymentTransactionMapper paymentTransactionMapperMock;

    @Mock
    private TransactionTypeRepository transactionTypeRepository;

    @Mock
    private VendorServiceImpl vendorService;

    @InjectMocks
    private PaymentTransactionServiceImpl paymentTransactionService;

    private PaymentTransaction paymentTransaction1;
    private PaymentTransactionDTO paymentTransactionDTO1;

    private PaymentTransaction paymentTransaction2;
    private PaymentTransactionDTO paymentTransactionDTO2;
    TransactionType transactionType;

    Vendor vendor1;

    Product product1;
    ProductDTO productDTO1;

    @BeforeEach
    void setUp() {

        ModelMapper modelMapper = new ModelMapper();
        ProductMapper productMapper = new ProductMapper(modelMapper);
        PaymentTransactionMapper paymentTransactionMapper = new PaymentTransactionMapper(modelMapper);

        transactionType = new TransactionType(2L, "Debit", 1, 2);

        vendor1 = new Vendor();
        vendor1.setId(1L);
        vendor1.setName("Vendor One");
        vendor1.setAddress("11 Long Ave, NYC");
        vendor1.setVendorId("EVID0001");

        paymentTransactionDTO1 = new PaymentTransactionDTO();
        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(vendor1);
        paymentTransactionDTO1.setTransactionType(transactionType);
        paymentTransactionDTO1.setTransactionNumber(1);
        paymentTransactionDTO1.setTransactionDescription("Payment Transaction Description 1");
        paymentTransactionDTO1.setTransactionAmount(50.50);
        paymentTransaction1 = paymentTransactionMapper.convertToEntity(paymentTransactionDTO1);

        productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setProductName("Product One");
        productDTO1.setProductDescription("Product One Description Test");
        productDTO1.setCreated(LocalDateTime.now());
        productDTO1.setModified(null);

        paymentTransactionDTO1.setProductDTO(productDTO1);


        paymentTransactionDTO2 = new PaymentTransactionDTO();
        paymentTransactionDTO2.setId(2L);
        paymentTransactionDTO2.setVendor(vendor1);
        paymentTransactionDTO2.setTransactionType(transactionType);
        paymentTransactionDTO2.setTransactionNumber(2);
        paymentTransactionDTO2.setTransactionDescription("Payment Transaction Description 2");
        paymentTransactionDTO2.setTransactionAmount(150.00);
        paymentTransaction2 = paymentTransactionMapper.convertToEntity(paymentTransactionDTO2);


    }

    @Test
    void getPaymentTransactionByIdWhenNotExist() {
        // given
        given(this.paymentTransactionRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException =
                assertThrows(PaymentTransactionNotFoundException.class, () -> this.paymentTransactionService.getPaymentTransactionById(1L));

        // then
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());
        assertEquals("Payment transaction doesn't exist!", globalErrorException.getMessage());

        verify(this.paymentTransactionRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToDTO(any(PaymentTransaction.class));
    }



    @Test
    void getPaymentTransactionByIdWhenExist() {
        // given
        given(this.paymentTransactionRepository.findById(any(Long.class))).willReturn(Optional.of(paymentTransaction1));
        given(this.paymentTransactionMapperMock.convertToDTO(any(PaymentTransaction.class))).willReturn(paymentTransactionDTO1);

        // when
        PaymentTransactionDTO paymentTransactionDTO = this.paymentTransactionService.getPaymentTransactionById(1L);

        // then

        assertEquals(paymentTransactionDTO1.getId(), paymentTransactionDTO.getId());
        assertEquals(paymentTransactionDTO1.getTransactionDescription(), paymentTransactionDTO.getTransactionDescription());
        assertEquals(paymentTransactionDTO1.getTransactionNumber(), paymentTransactionDTO.getTransactionNumber());
        assertEquals(paymentTransactionDTO1.getTransactionAmount(), paymentTransactionDTO.getTransactionAmount());

        assertDoesNotThrow(() -> new PaymentTransactionNotFoundException());

        verify(this.paymentTransactionRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(1)).convertToDTO(any(PaymentTransaction.class));
    }


    @Test
    void createPaymentTransactionWhenVendorNotExist() {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willThrow(new InvalidDataProvidedException("Invalid vendor provided!"));

        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.paymentTransactionService.createPaymentTransaction(paymentTransactionDTO1));

        // then
        assertEquals("Invalid vendor provided!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.transactionTypeRepository, times(0)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToEntity(any(PaymentTransactionDTO.class));
        verify(this.paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToDTO(any(PaymentTransaction.class));
    }

    @Test
    void createPaymentTransactionWhenInvalidTransactionType() {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.transactionTypeRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.paymentTransactionService.createPaymentTransaction(paymentTransactionDTO1));

        // then
        assertEquals("Invalid transaction type provided!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid vendor provided!"));

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.transactionTypeRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToEntity(any(PaymentTransactionDTO.class));
        verify(this.paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToDTO(any(PaymentTransaction.class));
    }

    @Test
    void createPaymentTransaction() {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.transactionTypeRepository.findById(any(Long.class))).willReturn(Optional.of(transactionType));
        given(this.paymentTransactionMapperMock.convertToEntity(any(PaymentTransactionDTO.class))).willReturn(paymentTransaction1);
        given(this.paymentTransactionRepository.save(any(PaymentTransaction.class))).willReturn(paymentTransaction1);
        given(this.paymentTransactionMapperMock.convertToDTO(any(PaymentTransaction.class))).willReturn(paymentTransactionDTO1);

        // when
        PaymentTransactionDTO paymentTransactionDTO = this.paymentTransactionService.createPaymentTransaction(paymentTransactionDTO1);

        // then

        assertEquals(paymentTransactionDTO1.getId(), paymentTransactionDTO.getId());
        assertEquals(paymentTransactionDTO1.getTransactionDescription(), paymentTransactionDTO.getTransactionDescription());
        assertEquals(paymentTransactionDTO1.getTransactionNumber(), paymentTransactionDTO.getTransactionNumber());
        assertEquals(paymentTransactionDTO1.getTransactionAmount(), paymentTransactionDTO.getTransactionAmount());

        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid vendor provided!"));
        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid transaction type provided!"));

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.transactionTypeRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(1)).convertToEntity(any(PaymentTransactionDTO.class));
        verify(this.paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
        verify(this.paymentTransactionMapperMock, times(1)).convertToDTO(any(PaymentTransaction.class));
    }



    @Test
    void updatePaymentTransactionWhenVendorNotExist() {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willThrow(new VendorNotFoundException());

        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.paymentTransactionService.updatePaymentTransaction(1L, paymentTransactionDTO1));

        // then
        assertEquals("Invalid vendor provided!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.transactionTypeRepository, times(0)).findById(any(Long.class));
        verify(this.paymentTransactionRepository, times(0)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToEntity(any(PaymentTransactionDTO.class));
        verify(this.paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToDTO(any(PaymentTransaction.class));
    }

    @Test
    void updatePaymentTransactionWhenInvalidTransactionType() {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.transactionTypeRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException = assertThrows(InvalidDataProvidedException.class, () -> this.paymentTransactionService.updatePaymentTransaction(1L, paymentTransactionDTO1));

        // then
        assertEquals("Invalid transaction type provided!", globalErrorException.getMessage());
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());

        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid vendor provided!"));

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.transactionTypeRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionRepository, times(0)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToEntity(any(PaymentTransactionDTO.class));
        verify(this.paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToDTO(any(PaymentTransaction.class));
    }

    @Test
    void updatePaymentTransactionWhenInvalidPaymentTransaction() {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.transactionTypeRepository.findById(any(Long.class))).willReturn(Optional.of(transactionType));
        given(this.paymentTransactionRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when

        GlobalErrorException globalErrorException = assertThrows(InvoiceNotFoundException.class, () -> this.paymentTransactionService.updatePaymentTransaction(1L, paymentTransactionDTO1));

        // then
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());
        assertEquals("Invoice doesn't exist!", globalErrorException.getMessage());

        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid vendor provided!"));
        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid transaction type provided!"));

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.transactionTypeRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToEntity(any(PaymentTransactionDTO.class));
        verify(this.paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToDTO(any(PaymentTransaction.class));
    }

    @Test
    void updatePaymentTransaction() {
        // given
        given(this.vendorService.getVendorById(any(Long.class))).willReturn(vendor1);
        given(this.transactionTypeRepository.findById(any(Long.class))).willReturn(Optional.of(transactionType));
        given(this.paymentTransactionRepository.findById(any(Long.class))).willReturn(Optional.of(paymentTransaction1));
        given(this.paymentTransactionMapperMock.convertToEntity(any(PaymentTransactionDTO.class))).willReturn(paymentTransaction1);
        given(this.paymentTransactionRepository.save(any(PaymentTransaction.class))).willReturn(paymentTransaction1);
        given(this.paymentTransactionMapperMock.convertToDTO(any(PaymentTransaction.class))).willReturn(paymentTransactionDTO1);

        // when
        PaymentTransactionDTO paymentTransactionDTO = this.paymentTransactionService.updatePaymentTransaction(1L, paymentTransactionDTO1);

        // then

        assertEquals(paymentTransactionDTO1.getId(), paymentTransactionDTO.getId());
        assertEquals(paymentTransactionDTO1.getTransactionDescription(), paymentTransactionDTO.getTransactionDescription());
        assertEquals(paymentTransactionDTO1.getTransactionNumber(), paymentTransactionDTO.getTransactionNumber());
        assertEquals(paymentTransactionDTO1.getTransactionAmount(), paymentTransactionDTO.getTransactionAmount());

        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid vendor provided!"));
        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid transaction type provided!"));

        verify(this.vendorService, times(1)).getVendorById(any(Long.class));
        verify(this.transactionTypeRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(1)).convertToEntity(any(PaymentTransactionDTO.class));
        verify(this.paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
        verify(this.paymentTransactionMapperMock, times(1)).convertToDTO(any(PaymentTransaction.class));
    }


    @Test
    void deletePaymentTransactionWhenInvalidId() {
        // given
        given(this.paymentTransactionRepository.findById(any(Long.class))).willThrow(new PaymentTransactionNotFoundException());

        // when
        GlobalErrorException globalErrorException =
                assertThrows(InvalidDataProvidedException.class, () -> this.paymentTransactionService.deletePaymentTransaction(1L));

        // then
        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());
        assertEquals("Invalid id provided!", globalErrorException.getMessage());

        verify(this.paymentTransactionRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionRepository, times(0)).deleteById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(0)).convertToDTO(any(PaymentTransaction.class));

    }

    @Test
    void deletePaymentTransaction() {
        // given
        given(this.paymentTransactionRepository.findById(any(Long.class))).willReturn(Optional.of(paymentTransaction1));
        given(this.paymentTransactionMapperMock.convertToDTO(any(PaymentTransaction.class))).willReturn(paymentTransactionDTO1);

        // when
        this.paymentTransactionService.deletePaymentTransaction(1L);

        // then
        assertDoesNotThrow(() -> new InvalidDataProvidedException("Invalid id provided!"));

        verify(this.paymentTransactionRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionRepository, times(1)).deleteById(any(Long.class));
        verify(this.paymentTransactionMapperMock, times(1)).convertToDTO(any(PaymentTransaction.class));

    }


}


