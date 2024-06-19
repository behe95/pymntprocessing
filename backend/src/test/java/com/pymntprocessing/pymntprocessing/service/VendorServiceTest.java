package com.pymntprocessing.pymntprocessing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;
import com.pymntprocessing.pymntprocessing.exception.GlobalErrorException;
import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.exception.VendorNotFoundException;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.repository.VendorRepository;
import com.pymntprocessing.pymntprocessing.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
@ExtendWith(MockitoExtension.class)
class VendorServiceTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;
    @Autowired
    private ObjectMapper objectMapper;
    private Vendor vendor1;
    private Vendor vendor2;

    @BeforeEach
    void setUp() {
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
    void getVendorByIdWhenNotExist() {
        // given
        given(this.vendorRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException =
                assertThrows(VendorNotFoundException.class, () -> this.vendorService.getVendorById(1L));

        // then
        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());
        assertEquals("Vendor doesn't exist!", globalErrorException.getMessage());

        verify(this.vendorRepository, times(1)).findById(any(Long.class));
    }


    @Test
    void getVendorByIdWhenExist() {
        vendor1.setId(1L);
        // given
        given(this.vendorRepository.findById(any(Long.class))).willReturn(Optional.of(vendor1));

        // when
        Vendor vendor = this.vendorService.getVendorById(1L);


        // then
        assertEquals(vendor1.getId(), vendor.getId());
        assertEquals(vendor1.getName(), vendor.getName());
        assertEquals(vendor1.getAddress(), vendor.getAddress());
        assertEquals(vendor1.getVendorId(), vendor.getVendorId());

        verify(this.vendorRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void updateVendorWhenNotExist() {
        vendor1.setId(1L);

        // given
        given(this.vendorRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException =
                assertThrows(VendorNotFoundException.class, () -> this.vendorService.updateVendor(1L, vendor1));

        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());
        assertEquals("Vendor doesn't exist!", globalErrorException.getMessage());

        // then
        verify(this.vendorRepository, times(1)).findById(any(Long.class));
        verify(this.vendorRepository, times(0)).save(any(Vendor.class));

    }

    @Test
    void updateVendor() {
        vendor1.setId(1L);

        // given
        given(this.vendorRepository.findById(any(Long.class))).willReturn(Optional.of(vendor1));
        given(this.vendorRepository.save(any(Vendor.class))).willReturn(vendor1);

        // when
        Vendor vendor = this.vendorService.updateVendor(1L, vendor1);

        // then
        assertEquals(vendor1.getId(), vendor.getId());
        assertEquals(vendor1.getName(), vendor.getName());
        assertEquals(vendor1.getAddress(), vendor.getAddress());
        assertEquals(vendor1.getVendorId(), vendor.getVendorId());
        verify(this.vendorRepository, times(1)).findById(any(Long.class));
        verify(this.vendorRepository, times(1)).save(any(Vendor.class));

    }


    @Test
    void deleteVendorWhenVendorIdNotProvided() {
        // when
        GlobalErrorException globalErrorException =
                assertThrows(InvalidDataProvidedException.class, () -> this.vendorService.deleteVendor(null));

        assertEquals(ErrorCodes.BAD_REQUEST.getErrorCode(), globalErrorException.getCode());
        assertEquals("Vendor id not provided!", globalErrorException.getMessage());

        // then
        verify(this.vendorRepository, times(0)).findById(any(Long.class));
        verify(this.vendorRepository, times(0)).deleteById(any(Long.class));


    }


    @Test
    void deleteVendorWhenNotExist() {
        vendor1.setId(1L);

        // given
        given(this.vendorRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when
        GlobalErrorException globalErrorException =
                assertThrows(VendorNotFoundException.class, () -> this.vendorService.deleteVendor(1L));

        assertEquals(ErrorCodes.NOT_FOUND.getErrorCode(), globalErrorException.getCode());
        assertEquals("Vendor doesn't exist!", globalErrorException.getMessage());

        // then
        verify(this.vendorRepository, times(1)).findById(any(Long.class));
        verify(this.vendorRepository, times(0)).deleteById(any(Long.class));


    }

    @Test
    void deleteVendor() {
        vendor1.setId(1L);

        // given
        given(this.vendorRepository.findById(any(Long.class))).willReturn(Optional.of(vendor1));

        // when
        this.vendorService.deleteVendor(1L);

        // then

        verify(this.vendorRepository, times(1)).findById(any(Long.class));
        verify(this.vendorRepository, times(1)).deleteById(any(Long.class));

    }
}