package com.pymntprocessing.pymntprocessing.service.impl;

import com.pymntprocessing.pymntprocessing.exception.ProductAssignedWithInvalidPaymentTransactionException;
import com.pymntprocessing.pymntprocessing.exception.ProductNotFoundException;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.mapper.ProductMapper;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import com.pymntprocessing.pymntprocessing.repository.ProductRepository;
import com.pymntprocessing.pymntprocessing.service.PaymentTransactionService;
import com.pymntprocessing.pymntprocessing.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final PaymentTransactionService paymentTransactionService;

    private final ProductMapper productMapper;


    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, PaymentTransactionService paymentTransactionService, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.paymentTransactionService = paymentTransactionService;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return this.productRepository.findAll().stream().map(this.productMapper::convertToDTO).toList();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return this.productRepository.findById(id).map(this.productMapper::convertToDTO).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {

        List<PaymentTransactionDTO> paymentTransactionDTOs = productDTO.getPaymentTransactionDTOs();

        if (Objects.isNull(paymentTransactionDTOs)) {
            paymentTransactionDTOs = new ArrayList<>();
            productDTO.setPaymentTransactionDTOs(paymentTransactionDTOs);
        }

        // make sure not to update instead of create
        productDTO.getPaymentTransactionDTOs().forEach(paymentTransactionDTO -> paymentTransactionDTO.setId(null));



        return this.productMapper.convertToDTO(this.productRepository.save(this.productMapper.convertToEntity(productDTO)));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Optional<Product> existingProduct = this.productRepository.findById(id);

        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException();
        }


        List<PaymentTransactionDTO> paymentTransactionDTOS = this.paymentTransactionService.getAllPaymentTransactionsByIds(
                productDTO.getPaymentTransactionDTOs().stream().map(PaymentTransactionDTO::getId).toList()
        );

        paymentTransactionDTOS.forEach(paymentTransactionDTO -> {
            if (Objects.nonNull(paymentTransactionDTO.getProductDTO()) &&
                    !Objects.equals(paymentTransactionDTO.getProductDTO().getId(), productDTO.getId())) {
                throw new ProductAssignedWithInvalidPaymentTransactionException();
            }
        });

        return this.productMapper.convertToDTO(this.productRepository.save(this.productMapper.convertToEntity(productDTO)));
    }

    @Override
    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }
}
