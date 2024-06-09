package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.Vendor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface VendorService {

    public List<Vendor> getAllVendors();

    public Vendor getVendorById(Long id);
    public Vendor createVendor(Vendor vendor);

    public Vendor updateVendor(Long id, Vendor vendor);

    public void deleteVendor(Long id);

}
