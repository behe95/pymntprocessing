package com.pymntprocessing.pymntprocessing.model.mapper;

import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper extends BaseMapper<Product, ProductDTO>{
    private final ModelMapper modelMapper;

    @Autowired
    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Product convertToEntity(ProductDTO dto) {
        Product product = this.modelMapper.map(dto, Product.class);

        List<PaymentTransaction> paymentTransactions = dto.getPaymentTransactionDTOs().stream().map(paymentTransactionDTO -> {
            PaymentTransaction paymentTransaction = this.modelMapper.map(paymentTransactionDTO, PaymentTransaction.class);
            paymentTransaction.setProduct(product);
            return paymentTransaction;
        }).toList();
        product.setPaymentTransactions(paymentTransactions);
        return product;
    }

    @Override
    public ProductDTO convertToDTO(Product entity) {
        ProductDTO productDTO = null;
        if (entity != null) {

            productDTO = this.modelMapper.map(entity, ProductDTO.class);
            List<PaymentTransactionDTO> paymentTransactionDTOS = entity
                    .getPaymentTransactions()
                    .stream()
                    .map(paymentTransaction -> this.modelMapper.map(paymentTransaction, PaymentTransactionDTO.class))
                    .toList();
            productDTO.setPaymentTransactionDTOs(paymentTransactionDTOS);

        }
        return productDTO;
    }
}
