package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.Vendor;
import com.pymntprocessing.pymntprocessing.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorServiceImpl implements VendorService{

    private final VendorRepository vendorRepository;

    @Autowired
    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }


    @Override
    public List<Vendor> getAllVendors() {
        return null;
    }

    @Override
    public Vendor getVendorById(Long id) {
        Optional<Vendor> vendor = this.vendorRepository.findById(id);
        return vendor.orElse(null);
    }

    @Override
    public Vendor createVendor(Vendor vendor) {
        return null;
    }

    @Override
    public Vendor updateVendor(Long id, Vendor vendor) {
        return null;
    }

    @Override
    public void deleteVendor(Long id) {

    }
}
