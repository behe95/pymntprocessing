package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.Vendor;

import java.util.List;

public interface VendorService {
    public Vendor saveVendor(Vendor vendor);
    public List<Vendor> getAllVendors();

    public void deleteVendorById(Integer vendorId);
}
