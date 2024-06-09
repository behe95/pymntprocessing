package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.Vendor;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor")
@CrossOrigin(origins = "http://localhost:3000")
public class VendorController {

    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }


    @PostMapping("/add")
    public String add(@RequestBody Vendor vendor) {
        this.vendorService.saveVendor(vendor);

        return vendor.getName() + " added to the system!";
    }

    @GetMapping("/get")
    public List<Vendor> getAll() {
        return this.vendorService.getAllVendors();
    }
}
