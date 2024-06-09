package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.ResponseMessage;
import com.pymntprocessing.pymntprocessing.model.Vendor;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/vendor")
@CrossOrigin(origins = "http://localhost:3000")
public class VendorController {

    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<Vendor>> getVednorById(@PathVariable Long id) {
        Vendor vendor = this.vendorService.getVendorById(id);
        if (vendor != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<Vendor>(vendor, true, ""));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage<>(null, false, "Vendor with id " + id + " is not found"));
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<List<Vendor>>> getAllVendors() {
        List<Vendor> vendors = this.vendorService.getAllVendors();
        if (vendors.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<List<Vendor>>(vendors, false, "Vendors not found!"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<List<Vendor>>(vendors, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<Vendor>> createVendor(@RequestBody Vendor vendor) {
        try {
            Vendor savedVendor = this.vendorService.createVendor(vendor);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage<Vendor>(vendor, true, "Vendor " + vendor.getName() + " created successfully!"));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<Vendor>(null, false, errorMessage));
        } catch (Exception ex) {
            String errorMessage = "ERROR: Internal Server Error!";

            errorMessage += ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage<Vendor>(null, false, errorMessage));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage<Vendor>> updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        try {
            Vendor updatedVendor = this.vendorService.updateVendor(id, vendor);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(updatedVendor, true, "Vendor " + vendor.getName() + " has been updated!"));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        } catch (Exception ex) {
            String errorMessage = "ERROR: Internal Server Error!";

            errorMessage += ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage<Vendor>> deleteVendor(@PathVariable Long id) {
        Vendor vendor = this.vendorService.getVendorById(id);

        if (vendor != null) {
            this.vendorService.deleteVendor(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(null, true, "Vendor " + vendor.getName() + " has been deleted"));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage<>(null, false, "Invalid id provided"));
    }
}
