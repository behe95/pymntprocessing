package com.pymntprocessing.pymntprocessing.controller;

import com.pymntprocessing.pymntprocessing.model.ResponseMessage;
import com.pymntprocessing.pymntprocessing.model.Vendor;
import com.pymntprocessing.pymntprocessing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
    @ResponseBody
    public ResponseMessage<Vendor> add(@RequestBody Vendor vendor) {
        Vendor savedVendor = this.vendorService.saveVendor(vendor);
        ResponseMessage<Vendor> responseMessage = new ResponseMessage<>();

        if (Objects.nonNull(savedVendor)) {
            responseMessage.setData(savedVendor);
            responseMessage.setMessage(vendor.getName() + " added to the system!");
        }
        return responseMessage;
    }

    @GetMapping("/get")
    public List<Vendor> getAll() {
        return this.vendorService.getAllVendors();
    }


    @DeleteMapping("/delete/{vendorId}")
    @ResponseBody
    public String deleteVendorById(@PathVariable Integer vendorId) {

        this.vendorService.deleteVendorById(vendorId);

        return "Vendor has been deleted!";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseMessage<Vendor> update(@RequestBody Vendor vendor) {
        Vendor savedVendor = this.vendorService.saveVendor(vendor);
        ResponseMessage<Vendor> responseMessage = new ResponseMessage<>();

        if (Objects.nonNull(savedVendor)) {
            responseMessage.setData(savedVendor);
            responseMessage.setMessage(vendor.getName() + " updated in the system!");
        }
        return responseMessage;
    }
}
