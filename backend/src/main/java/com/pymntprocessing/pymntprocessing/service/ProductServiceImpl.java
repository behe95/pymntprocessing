package com.pymntprocessing.pymntprocessing.service;

import com.pymntprocessing.pymntprocessing.model.mapper.ProductMapper;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import com.pymntprocessing.pymntprocessing.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return this.productRepository.findAll().stream().map(this.productMapper::convertToDTO).toList();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return this.productRepository.findById(id).map(this.productMapper::convertToDTO).orElse(null);
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        return this.productMapper.convertToDTO(this.productRepository.save(this.productMapper.convertToEntity(productDTO)));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Optional<Product> existingProduct = this.productRepository.findById(id);
        if (existingProduct.isPresent()) {
            return this.productMapper.convertToDTO(this.productRepository.save(this.productMapper.convertToEntity(productDTO)));
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }
}
