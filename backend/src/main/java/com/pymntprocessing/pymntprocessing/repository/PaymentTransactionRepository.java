package com.pymntprocessing.pymntprocessing.repository;

import com.pymntprocessing.pymntprocessing.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findById(Long id);
    List<PaymentTransaction> findAllByVendorId(Long id);
}
