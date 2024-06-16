package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.repository.VendorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
        return this.vendorRepository.findAll();
    }

    @Override
    public Vendor getVendorById(Long id) {
        Optional<Vendor> vendor = this.vendorRepository.findById(id);
        return vendor.orElse(null);
    }

    @Override
    public Vendor createVendor(Vendor vendor) {
        return this.vendorRepository.save(vendor);
    }

    @Override
    public Vendor updateVendor(Long id, Vendor vendor) {
        Optional<Vendor> existingVendor = this.vendorRepository.findById(id);
        if (existingVendor.isPresent()) {
            return this.vendorRepository.save(vendor);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        this.vendorRepository.deleteById(id);
    }
}
