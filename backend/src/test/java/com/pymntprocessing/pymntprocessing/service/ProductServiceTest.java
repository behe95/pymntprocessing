package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;
import com.pymntprocessing.pymntprocessing.exception.GlobalErrorException;
import com.pymntprocessing.pymntprocessing.exception.ProductAssignedWithInvalidPaymentTransactionException;
import com.pymntprocessing.pymntprocessing.exception.ProductNotFoundException;
import com.pymntprocessing.pymntprocessing.exception.VendorNotFoundException;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.model.mapper.PaymentTransactionMapper;
import com.pymntprocessing.pymntprocessing.model.mapper.ProductMapper;
import com.pymntprocessing.pymntprocessing.repository.ProductRepository;
import com.pymntprocessing.pymntprocessing.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentTransactionService paymentTransactionService;

    @Mock
    private ProductMapper productMapperMock;

    @InjectMocks
    private ProductServiceImpl productService;


    Product product1;
    ProductDTO productDTO1;

    Product product2;
    ProductDTO productDTO2;
    PaymentTransactionDTO paymentTransactionDTO1;
    TransactionType transactionType;

    Vendor vendor;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        ProductMapper productMapper = new ProductMapper(modelMapper);


        transactionType = new TransactionType(1L, "Debit", 1, 2);
        vendor = new Vendor();
        vendor.setId(1L);
        vendor.setName("Vendor One");
        vendor.setAddress("11 Long Ave, NYC");
        vendor.setVendorId("EVID0001");


        productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setProductName("Product One");
        productDTO1.setProductDescription("Product One Description Test");
        productDTO1.setCreated(LocalDateTime.now());
        productDTO1.setModified(null);

        paymentTransactionDTO1 = new PaymentTransactionDTO();
        paymentTransactionDTO1.setId(1L);
        paymentTransactionDTO1.setVendor(vendor);
        paymentTransactionDTO1.setTransactionType(transactionType);
        paymentTransactionDTO1.setTransactionNumber(2);
        paymentTransactionDTO1.setTransactionDescription("Payment Transaction Description 1");
        paymentTransactionDTO1.setTransactionAmount(150.00);


        productDTO1.setPaymentTransactionDTOs(List.of(paymentTransactionDTO1));

        product1 = productMapper.convertToEntity(productDTO1);

        productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setProductName("Product Two");
        productDTO2.setProductDescription("Product Two Description Test");
        productDTO2.setCreated(LocalDateTime.now().minusDays(11));
        productDTO2.setModified(LocalDateTime.now());
        productDTO2.setPaymentTransactionDTOs(List.of());

        product2 = productMapper.convertToEntity(productDTO2);
    }

    @Test
    void getProductByIdWhenNotExist() {
        // given
        given(this.productRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException =
                assertThrows(ProductNotFoundException.class, () -> this.productService.getProductById(1L));

        // then
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());
        assertEquals("Product doesn't exist!", globalErrorException.getMessage());

        verify(this.productRepository, times(1)).findById(any(Long.class));
        verify(this.productMapperMock, times(0)).convertToDTO(any(Product.class));

    }


    @Test
    void getProductByIdWhenExist() {
        productDTO1.setId(1L);
        // given
        given(this.productRepository.findById(any(Long.class))).willReturn(Optional.of(product1));
        given(this.productMapperMock.convertToDTO(product1)).willReturn(productDTO1);

        // when
        ProductDTO productDTO = this.productService.getProductById(1L);


        // then
        assertEquals(productDTO1.getId(), productDTO.getId());
        assertEquals(productDTO1.getProductName(), productDTO.getProductName());
        assertEquals(productDTO1.getProductDescription(), productDTO.getProductDescription());
        assertEquals(productDTO1.getCreated(), productDTO.getCreated());
        assertEquals(productDTO1.getModified(), productDTO.getModified());
        assertEquals(productDTO1.getPaymentTransactionDTOs().size(), productDTO.getPaymentTransactionDTOs().size());

        verify(this.productRepository, times(1)).findById(any(Long.class));
        verify(this.productMapperMock, times(1)).convertToDTO(any(Product.class));
    }

    @Test
    void createProductVerifyPaymentTransactionWithNoId() {

        ModelMapper modelMapper = new ModelMapper();
        ProductMapper productMapper = new ProductMapper(modelMapper);
        PaymentTransactionMapper paymentTransactionMapper = new PaymentTransactionMapper(modelMapper);

        Product savedProduct = productMapper.convertToEntity(productDTO1);
        PaymentTransaction savedPaymentTransaction = paymentTransactionMapper.convertToEntity(paymentTransactionDTO1);
        savedProduct.setPaymentTransactions(List.of(savedPaymentTransaction));

        ProductDTO savedProductDTO = productMapper.convertToDTO(savedProduct);

        // given
        given(this.productMapperMock.convertToEntity(any(ProductDTO.class))).willReturn(product1);
        given(this.productRepository.save(any(Product.class))).willReturn(savedProduct);
        given(this.productMapperMock.convertToDTO(any(Product.class))).willReturn(savedProductDTO);

        // when
        productDTO1.setId(null);
        ProductDTO productDTO = this.productService.createProduct(productDTO1);

        // then

        ArgumentCaptor<ProductDTO> productArgumentDTOCaptor = ArgumentCaptor.forClass(ProductDTO.class);

        verify(this.productMapperMock, times(1)).convertToEntity(productArgumentDTOCaptor.capture());
        verify(this.productRepository, times(1)).save(any(Product.class));
        verify(this.productMapperMock, times(1)).convertToDTO(any(Product.class));

        productArgumentDTOCaptor.getValue().getPaymentTransactionDTOs().forEach(paymentTransactionDTO -> {
            assertNull(paymentTransactionDTO.getId());
        });
    }

    @Test
    void updateProductWhenProductNotExist() {
        // given
        given(this.productRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException = assertThrows(ProductNotFoundException.class, () -> this.productService.updateProduct(1L, productDTO1));

        // then
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());
        assertEquals("Product doesn't exist!", globalErrorException.getMessage());

        verify(this.productRepository, times(1)).findById(any(Long.class));
        verify(this.paymentTransactionService, times(0)).getAllPaymentTransactionsByIds(any(List.class));
        assertDoesNotThrow(() -> new ProductAssignedWithInvalidPaymentTransactionException());
        verify(this.productMapperMock, times(0)).convertToEntity(any(ProductDTO.class));
        verify(this.productRepository, times(0)).save(any(Product.class));
        verify(this.productMapperMock, times(0)).convertToDTO(any(Product.class));
    }

    @Test
    void updateProductWithPaymentTransactionsThatAlreadyHaveAnotherProductAssigned() {

        paymentTransactionDTO1.setProductDTO(productDTO2);
        List<PaymentTransactionDTO> paymentTransactionDTOList = List.of(paymentTransactionDTO1);

        // given
        given(this.productRepository.findById(any(Long.class))).willReturn(Optional.of(product1));
        given(this.paymentTransactionService.getAllPaymentTransactionsByIds(anyList())).willReturn(paymentTransactionDTOList);

        // when
        GlobalErrorException globalErrorException = assertThrows(ProductAssignedWithInvalidPaymentTransactionException.class, () ->
                this.productService.updateProduct(1L, productDTO1));

        // then
        verify(this.productRepository,times(1)).findById(any(Long.class));
        assertDoesNotThrow(() -> new ProductNotFoundException());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransactionsByIds(anyList());

        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());
        assertEquals("Unable to assign payment transaction to product", globalErrorException.getMessage());

        verify(this.productMapperMock, times(0)).convertToEntity(any(ProductDTO.class));
        verify(this.productRepository, times(0)).save(any(Product.class));
        verify(this.productMapperMock, times(0)).convertToDTO(any(Product.class));
    }


    @Test
    void updateProduct() {
        ModelMapper modelMapper = new ModelMapper();
        ProductMapper productMapper = new ProductMapper(modelMapper);

        paymentTransactionDTO1.setProductDTO(productDTO1);
        List<PaymentTransactionDTO> paymentTransactionDTOList = List.of(paymentTransactionDTO1);

        // given
        given(this.productRepository.findById(any(Long.class))).willReturn(Optional.of(product1));
        given(this.paymentTransactionService.getAllPaymentTransactionsByIds(anyList())).willReturn(paymentTransactionDTOList);

        Product product = productMapper.convertToEntity(productDTO1);
        given(this.productMapperMock.convertToEntity(productDTO1)).willReturn(product);

        given(this.productRepository.save(product)).willReturn(product);

        ProductDTO updatedProductDTO = productMapper.convertToDTO(product);
        given(this.productMapperMock.convertToDTO(product)).willReturn(updatedProductDTO);

        // when
        ProductDTO productDTO = this.productService.updateProduct(1L, productDTO1);

        // then
        verify(this.productRepository,times(1)).findById(any(Long.class));
        assertDoesNotThrow(() -> new ProductNotFoundException());
        verify(this.paymentTransactionService, times(1)).getAllPaymentTransactionsByIds(anyList());
        assertDoesNotThrow(() -> new ProductAssignedWithInvalidPaymentTransactionException());

        verify(this.productMapperMock, times(1)).convertToEntity(any(ProductDTO.class));
        verify(this.productRepository, times(1)).save(any(Product.class));
        verify(this.productMapperMock, times(1)).convertToDTO(any(Product.class));
    }
}