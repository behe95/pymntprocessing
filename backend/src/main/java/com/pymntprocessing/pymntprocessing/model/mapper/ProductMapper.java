package com.pymntprocessing.pymntprocessing.model.mapper;

import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper extends BaseMapper<Product, ProductDTO>{
    private final ModelMapper modelMapper;

    @Autowired
    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Product convertToEntity(ProductDTO dto) {
        Product product = null;

        if (dto != null) {
            product = this.modelMapper.map(dto, Product.class);

            if (dto.getPaymentTransactionDTO() != null) {
                PaymentTransaction paymentTransaction = this.modelMapper.map(dto.getPaymentTransactionDTO(), PaymentTransaction.class);
                paymentTransaction.setId(product.getPaymentTransaction().getId());
                paymentTransaction.setProduct(product);
                product.setPaymentTransaction(paymentTransaction);
            }
        }
        return product;
    }

    @Override
    public ProductDTO convertToDTO(Product entity) {
        ProductDTO productDTO = null;
        if (entity != null) {

            productDTO = this.modelMapper.map(entity, ProductDTO.class);

            if (entity.getPaymentTransaction() != null) {
                productDTO.setPaymentTransactionDTO(this.modelMapper.map(entity.getPaymentTransaction(), PaymentTransactionDTO.class));
            }
        }
        return productDTO;
    }
}
