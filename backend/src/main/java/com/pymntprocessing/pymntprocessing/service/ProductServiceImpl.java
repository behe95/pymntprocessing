package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.dto.ProductConverter;
import com.pymntprocessing.pymntprocessing.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.entity.Product;
import com.pymntprocessing.pymntprocessing.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final ProductConverter productConverter;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductConverter productConverter) {
        this.productRepository = productRepository;
        this.productConverter = productConverter;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return this.productRepository.findAll().stream().map(this.productConverter::toDTO).toList();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Optional<Product> product = this.productRepository.findById(id);
        ProductDTO productDTO = null;

        if (product.isPresent()) {
            productDTO = this.productConverter.toDTO(product.get());
        }

        return productDTO;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        return this.productConverter.toDTO(this.productRepository.save(this.productConverter.toEntity(productDTO)));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Optional<Product> existingProduct = this.productRepository.findById(id);
        if (existingProduct.isPresent()) {
            return this.productConverter.toDTO(this.productRepository.save(this.productConverter.toEntity(productDTO)));
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }
}
