package com.pymntprocessing.pymntprocessing.service.impl;

import com.pymntprocessing.pymntprocessing.exception.InvalidDataProvidedException;
import com.pymntprocessing.pymntprocessing.exception.VendorNotFoundException;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.repository.VendorRepository;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorServiceImpl implements VendorService {

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
        return this.vendorRepository.findById(id).orElseThrow(VendorNotFoundException::new);
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
        throw new VendorNotFoundException();
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        if (id == null) {
            throw new InvalidDataProvidedException("Vendor id not provided!");
        }
        this.getVendorById(id);

        this.vendorRepository.deleteById(id);
    }
}
