package com.pymntprocessing.pymntprocessing.controller;


import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import com.pymntprocessing.pymntprocessing.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(ApiConstants.V1.Product.PRODUCT_PATH)
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;

    private final PaymentTransactionService paymentTransactionService;

    @Autowired
    public ProductController(ProductService productService, PaymentTransactionService paymentTransactionService) {
        this.productService = productService;
        this.paymentTransactionService = paymentTransactionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsePayload<ProductDTO>> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = this.productService.getProductById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(productDTO, true, ""));
    }
    @GetMapping
    public ResponseEntity<ResponsePayload<List<ProductDTO>>> getAllProduct() {
        List<ProductDTO> productDTOS = this.productService.getAllProducts();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(productDTOS, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<ProductDTO>> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO newProductDTO = this.productService.createProduct(productDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponsePayload<ProductDTO>(newProductDTO, true, "Product created!"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<ProductDTO>> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {

        String errorMessage = "";
        /**
         * validate data
         */
        if (id == null || productDTO.getId() == null || !id.equals(productDTO.getId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, "Product id not provided!"));
        } else {
            ProductDTO existingProductDTO = this.productService.getProductById(id);
            if (existingProductDTO == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponsePayload<>(null, false, "Product doesn't exist!"));
            }
        }

        ProductDTO newProductDTO = this.productService.updateProduct(id, productDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<ProductDTO>(newProductDTO, true, "Product updated!"));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponsePayload<PaymentTransactionDTO>> deletePaymentTransaction(@PathVariable Long id) {
        ProductDTO existingProductDTO = this.productService.getProductById(id);
        if (existingProductDTO == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponsePayload<>(null, false, "Product doesn't exist!"));
        }



        PaymentTransactionDTO paymentTransactionDTO = existingProductDTO.getPaymentTransactionDTO();

        if (paymentTransactionDTO != null && paymentTransactionDTO.getId() != null) {

            PaymentTransactionDTO existingPaymentTransaction = this.paymentTransactionService.getPaymentTransactionById(paymentTransactionDTO.getId());

            existingPaymentTransaction.setProductDTO(null);
            this.paymentTransactionService.updatePaymentTransaction(paymentTransactionDTO.getId(), existingPaymentTransaction);
        }


        this.productService.deleteProduct(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponsePayload<>(null, true, "Product " + existingProductDTO.getProductName() + " has been deleted"));
    }


}

