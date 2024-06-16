package com.pymntprocessing.pymntprocessing.service;



import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    public List<ProductDTO> getAllProducts();

    public ProductDTO getProductById(Long id);
    public ProductDTO createProduct(ProductDTO productDTO);

    public ProductDTO updateProduct(Long id, ProductDTO productDTO);

    public void deleteProduct(Long id);
}
