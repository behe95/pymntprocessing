package com.pymntprocessing.pymntprocessing.dto;

import com.pymntprocessing.pymntprocessing.entity.Invoice;
import com.pymntprocessing.pymntprocessing.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.entity.Product;
import com.pymntprocessing.pymntprocessing.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {
    private final ModelMapper modelMapper;

    @Autowired
    public ProductConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ProductDTO toDTO(Product product) {
        ProductDTO productDTO = null;
        if (product != null) {

            productDTO = this.modelMapper.map(product, ProductDTO.class);

            if (product.getPaymentTransaction() != null) {
                productDTO.setPaymentTransactionDTO(this.modelMapper.map(product.getPaymentTransaction(), PaymentTransactionDTO.class));
            }
        }
        return productDTO;
    }

    public Product toEntity(ProductDTO productDTO) {
        Product product = null;

        if (productDTO != null) {
            product = this.modelMapper.map(productDTO, Product.class);

            if (productDTO.getPaymentTransactionDTO() != null) {
                product.setPaymentTransaction(this.modelMapper.map(productDTO.getPaymentTransactionDTO(), PaymentTransaction.class));
            }
        }
        return product;
    }
}
