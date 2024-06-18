package com.pymntprocessing.pymntprocessing.model.mapper;

import com.pymntprocessing.pymntprocessing.model.dto.InvoiceDTO;
import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.Invoice;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PaymentTransactionMapper extends BaseMapper<PaymentTransaction, PaymentTransactionDTO> {
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PaymentTransactionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public PaymentTransaction convertToEntity(PaymentTransactionDTO dto) {
        PaymentTransaction paymentTransaction = null;

        if (dto != null) {
            paymentTransaction = this.modelMapper.map(dto, PaymentTransaction.class);
            if (dto.getInvoiceDTO() != null) {
                paymentTransaction.setInvoice(this.modelMapper.map(dto.getInvoiceDTO(), Invoice.class));
            }

            if (dto.getProductDTO() != null) {
                Product product;

                if (dto.getProductDTO().getId() != null) {
                    product = this.entityManager.find(Product.class, dto.getProductDTO().getId());

                    boolean isFound = false;

                    for (PaymentTransaction pt : product.getPaymentTransactions()) {
                        if (Objects.equals(pt.getId(), dto.getId())) {
                            this.modelMapper.map(paymentTransaction, pt);
                            isFound = true;
                            break;
                        }
                    }

                    if (!isFound) {
                        product.getPaymentTransactions().add(paymentTransaction);
                    }
                } else {
                    product = this.modelMapper.map(dto.getProductDTO(), Product.class);
                }

                product.setPaymentTransactions(product.getPaymentTransactions());
                paymentTransaction.setProduct(product);
            }
        }
        return paymentTransaction;
    }

    @Override
    public PaymentTransactionDTO convertToDTO(PaymentTransaction entity) {
        PaymentTransactionDTO paymentTransactionDTO = null;
        if (entity != null) {
            paymentTransactionDTO = this.modelMapper.map(entity, PaymentTransactionDTO.class);
            if (entity.getInvoice() != null) {
                paymentTransactionDTO.setInvoiceDTO(this.modelMapper.map(entity.getInvoice(), InvoiceDTO.class));
            }

            if (entity.getProduct() != null) {
                paymentTransactionDTO.setProductDTO(this.modelMapper.map(entity.getProduct(), ProductDTO.class));
            }
        }
        return paymentTransactionDTO;
    }
}
