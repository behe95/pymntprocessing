package com.pymntprocessing.pymntprocessing.repository;

import com.pymntprocessing.pymntprocessing.model.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
}
