package com.pymntprocessing.pymntprocessing.repository;

import com.pymntprocessing.pymntprocessing.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Long> {
}
