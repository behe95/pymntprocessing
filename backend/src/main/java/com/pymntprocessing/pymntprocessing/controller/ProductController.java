package com.pymntprocessing.pymntprocessing.controller;


import com.pymntprocessing.pymntprocessing.constant.ApiConstants;
import com.pymntprocessing.pymntprocessing.constant.db.TransactionTypeValue;
import com.pymntprocessing.pymntprocessing.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.entity.ResponseMessage;
import com.pymntprocessing.pymntprocessing.entity.TransactionType;
import com.pymntprocessing.pymntprocessing.entity.Vendor;
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
    @GetMapping
    public ResponseEntity<ResponseMessage<List<ProductDTO>>> getAllProduct() {
        List<ProductDTO> productDTOS = this.productService.getAllProducts();

        if (productDTOS.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage<>(null, false, "Products not found"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage<>(productDTOS, true, ""));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<ProductDTO>> createProduct(@RequestBody ProductDTO productDTO) {
        PaymentTransactionDTO paymentTransactionDTO = productDTO.getPaymentTransactionDTO();

        String errorMessage = "";
        /**
         * validate data
         */
        if (paymentTransactionDTO != null && paymentTransactionDTO.getId() != null) {

            PaymentTransactionDTO existingPaymentTransaction = this.paymentTransactionService.getPaymentTransactionById(paymentTransactionDTO.getId());

            if (existingPaymentTransaction.getProductDTO() != null) {
                errorMessage = "Unable to assign payment transaction " + existingPaymentTransaction.getTransactionNumber() + " to Product " + productDTO.getProductName();

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseMessage<>(null, false, errorMessage));
            }
        }

        try {
            ProductDTO newProductDTO = this.productService.createProduct(productDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseMessage<ProductDTO>(newProductDTO, true, "Product created!"));
        } catch (DataIntegrityViolationException e) {
            errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        } catch (Exception ex) {
            errorMessage = "ERROR: Internal Server Error! " + ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage<ProductDTO>> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        PaymentTransactionDTO paymentTransactionDTO = productDTO.getPaymentTransactionDTO();

        String errorMessage = "";
        /**
         * validate data
         */
        if (id == null || productDTO.getId() == null || !id.equals(productDTO.getId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, "Product id not provided!"));
        } else {
            ProductDTO existingProductDTO = this.productService.getProductById(id);
            if (existingProductDTO == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseMessage<>(null, false, "Product doesn't exist!"));
            }
        }

        if (paymentTransactionDTO != null && paymentTransactionDTO.getId() != null) {

            PaymentTransactionDTO existingPaymentTransaction = this.paymentTransactionService.getPaymentTransactionById(paymentTransactionDTO.getId());

            if (existingPaymentTransaction.getProductDTO() != null) {
                errorMessage = "Unable to assign payment transaction " + existingPaymentTransaction.getTransactionNumber() + " to Product " + productDTO.getProductName();

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseMessage<>(null, false, errorMessage));
            }
        }

        try {
            ProductDTO newProductDTO = this.productService.updateProduct(id, productDTO);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage<ProductDTO>(newProductDTO, true, "Product updated!"));
        } catch (DataIntegrityViolationException e) {
            errorMessage = "ERROR: Duplicate entry!";
            if (e.getRootCause() != null) {
                errorMessage = e.getRootCause().getMessage();
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        } catch (Exception ex) {
            errorMessage = "ERROR: Internal Server Error! " + ex.getMessage();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage<>(null, false, errorMessage));
        }

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage<PaymentTransactionDTO>> deletePaymentTransaction(@PathVariable Long id) {
        ProductDTO existingProductDTO = this.productService.getProductById(id);
        if (existingProductDTO == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage<>(null, false, "Product doesn't exist!"));
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
                .body(new ResponseMessage<>(null, true, "Product " + existingProductDTO.getProductName() + " has been deleted"));
    }


}

