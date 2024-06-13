package com.pymntprocessing.pymntprocessing.controller;


import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.entity.ResponseMessage;
import com.pymntprocessing.pymntprocessing.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.V1.Product.PRODUCT_PATH)
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<ProductDTO>> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = this.productService.getProductById(id);

        if (productDTO != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<>(productDTO, true, ""));
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseMessage<>(null, false, "Product not found!"));
    }
}
