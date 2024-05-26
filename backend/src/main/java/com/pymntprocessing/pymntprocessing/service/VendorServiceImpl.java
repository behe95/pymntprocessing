package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.Vendor;
import com.pymntprocessing.pymntprocessing.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorServiceImpl implements VendorService{

    private final VendorRepository vendorRepository;

    @Autowired
    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Vendor saveVendor(Vendor vendor) {
        return this.vendorRepository.save(vendor);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return this.vendorRepository.findAll();
    }
}
