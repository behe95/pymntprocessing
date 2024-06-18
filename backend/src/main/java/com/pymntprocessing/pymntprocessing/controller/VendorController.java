package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.V1.Vendor.VENDOR_PATH)
@CrossOrigin(origins = "http://localhost:3000")
public class VendorController {

    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsePayload<Vendor>> getVendorById(@PathVariable Long id) {
        Vendor vendor = this.vendorService.getVendorById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<Vendor>(vendor, true, ""));
    }

    @GetMapping
    public ResponseEntity<ResponsePayload<List<Vendor>>> getAllVendors() {
        List<Vendor> vendors = this.vendorService.getAllVendors();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<Vendor>>(vendors, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<Vendor>> createVendor(@RequestBody Vendor vendor) {
        Vendor savedVendor = this.vendorService.createVendor(vendor);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponsePayload<Vendor>(savedVendor, true, "Vendor " + vendor.getName() + " created successfully!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<Vendor>> updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        Vendor updatedVendor = this.vendorService.updateVendor(id, vendor);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(updatedVendor, true, "Vendor " + vendor.getName() + " has been updated!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponsePayload<Vendor>> deleteVendor(@PathVariable Long id) {
        this.vendorService.deleteVendor(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(null, true, "Vendor has been deleted"));
    }
}
