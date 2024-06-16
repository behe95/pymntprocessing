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
        if (vendor != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponsePayload<Vendor>(vendor, true, ""));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponsePayload<>(null, false, "Vendor with id " + id + " is not found"));
    }

    @GetMapping
    public ResponseEntity<ResponsePayload<List<Vendor>>> getAllVendors() {
        List<Vendor> vendors = this.vendorService.getAllVendors();
        if (vendors.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponsePayload<List<Vendor>>(vendors, false, "Vendors not found!"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<List<Vendor>>(vendors, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<Vendor>> createVendor(@RequestBody Vendor vendor) {
        try {
            Vendor savedVendor = this.vendorService.createVendor(vendor);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponsePayload<Vendor>(vendor, true, "Vendor " + vendor.getName() + " created successfully!"));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<Vendor>(null, false, errorMessage));
        } catch (Exception ex) {
            String errorMessage = "ERROR: Internal Server Error!";

            errorMessage += ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsePayload<Vendor>(null, false, errorMessage));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<Vendor>> updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        try {
            Vendor updatedVendor = this.vendorService.updateVendor(id, vendor);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponsePayload<>(updatedVendor, true, "Vendor " + vendor.getName() + " has been updated!"));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        } catch (Exception ex) {
            String errorMessage = "ERROR: Internal Server Error!";

            errorMessage += ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponsePayload<>(null, false, errorMessage));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponsePayload<Vendor>> deleteVendor(@PathVariable Long id) {
        Vendor vendor = this.vendorService.getVendorById(id);

        if (vendor != null) {
            this.vendorService.deleteVendor(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponsePayload<>(null, true, "Vendor " + vendor.getName() + " has been deleted"));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponsePayload<>(null, false, "Invalid id provided"));
    }
}
