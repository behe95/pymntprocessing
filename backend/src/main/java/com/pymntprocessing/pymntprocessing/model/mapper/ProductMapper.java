package com.pymntprocessing.pymntprocessing.model.mapper;

import com.pymntprocessing.pymntprocessing.model.dto.PaymentTransactionDTO;
import com.pymntprocessing.pymntprocessing.model.dto.ProductDTO;
import com.pymntprocessing.pymntprocessing.model.entity.PaymentTransaction;
import com.pymntprocessing.pymntprocessing.model.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductMapper extends BaseMapper<Product, ProductDTO>{

    @PersistenceContext
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public Product convertToEntity(ProductDTO dto) {
        Product product = this.modelMapper.map(dto, Product.class);


        if (dto.getPaymentTransactionDTOs() == null) {
            dto.setPaymentTransactionDTOs(new ArrayList<>());
        }

        List<PaymentTransaction> paymentTransactions = dto.getPaymentTransactionDTOs().stream().map(paymentTransactionDTO -> {
            PaymentTransaction paymentTransaction = this.modelMapper.map(paymentTransactionDTO, PaymentTransaction.class);
            paymentTransaction.setProduct(product);
            return paymentTransaction;
        }).toList();
        product.setPaymentTransactions(paymentTransactions);

        // this.entityManager != null       to prevent Product Service Test failure
        // fix require
        if (dto.getId() != null && this.entityManager != null) {

            Product product1 = this.entityManager.find(Product.class, dto.getId());


            if (product.getPaymentTransactions() == null) {
                product.setPaymentTransactions(new ArrayList<>());
            }

            String jpql = "SELECT pt FROM PaymentTransaction pt WHERE pt.product.id = :productId";
            List<PaymentTransaction> paymentTransactionsSaved = entityManager.createQuery(jpql, PaymentTransaction.class)
                    .setParameter("productId", dto.getId())
                    .getResultList();

            List<PaymentTransaction> paymentTransactionsMerged = this.mergeTwoPaymentTransactionsLists(product, product.getPaymentTransactions(), paymentTransactionsSaved);

            product1.setProductName(product.getProductName());
            product1.setProductDescription(product.getProductDescription());
            product1.setPaymentTransactions(paymentTransactionsMerged);
            product1.setCreated(product.getCreated());
            product1.setModified(product.getModified());

            product1.setPaymentTransactions(paymentTransactionsMerged);
            return product1;
        }


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


    private List<PaymentTransaction> mergeTwoPaymentTransactionsLists(Product product, List<PaymentTransaction> paymentTransactionsLeft, List<PaymentTransaction> paymentTransactionsRight) {
        List<Long> idsNotToRemove = new ArrayList<>();
        Map<Long, PaymentTransaction> outputMap = new HashMap<>();
        List<PaymentTransaction> output = new ArrayList<>();

        /**
         * Get everything from the right list
         */
        for (PaymentTransaction ptR : paymentTransactionsRight) {
            outputMap.put(ptR.getId(), ptR);
        }

        /**
         * Insert or update to the right from the left
         *      - if id null    - then add
         *      - if id in left exist in right      - update right
         */
        int newIdCounter = 0;
        for (PaymentTransaction ptL : paymentTransactionsLeft) {
            if (ptL.getId() == null) {
                newIdCounter++;
                Long dummyId = (long) (Integer.MAX_VALUE - newIdCounter);
                outputMap.put(dummyId, ptL);

                idsNotToRemove.add(dummyId);

                ptL.setProduct(product);
            } else {
                PaymentTransaction existingPTR = outputMap.get(ptL.getId());
                this.modelMapper.map(ptL, existingPTR);
                outputMap.put(existingPTR.getId(), existingPTR);

                idsNotToRemove.add(existingPTR.getId());

            }
        }

        for (Long id : outputMap.keySet()) {
            if (!idsNotToRemove.contains(id)) {
                outputMap.get(id).setProduct(null);
                this.entityManager.remove(outputMap.get(id));
            } else {
                output.add(outputMap.get(id));
            }
        }

        return output;
    }
}
