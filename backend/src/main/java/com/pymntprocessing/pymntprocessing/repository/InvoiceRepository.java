package com.pymntprocessing.pymntprocessing.repository;

import com.pymntprocessing.pymntprocessing.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByVendorId(Long id);
}
