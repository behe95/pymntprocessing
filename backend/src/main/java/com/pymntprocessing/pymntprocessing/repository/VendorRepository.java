package com.pymntprocessing.pymntprocessing.repository;

import com.pymntprocessing.pymntprocessing.model.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Optional<Vendor> findById(Long id);
    void deleteById(Long id);
}
