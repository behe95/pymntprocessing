package com.pymntprocessing.pymntprocessing.service;



import com.pymntprocessing.pymntprocessing.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.entity.Product;

import java.util.List;

public interface ProductService {

    public List<ProductDTO> getAllProducts();

    public ProductDTO getProductById(Long id);
    public ProductDTO createProduct(ProductDTO productDTO);

    public ProductDTO updateProduct(Long id, ProductDTO productDTO);

    public void deleteProduct(Long id);
}
