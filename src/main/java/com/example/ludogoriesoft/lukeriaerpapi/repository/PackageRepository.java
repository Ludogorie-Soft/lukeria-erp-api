package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {
    List<Package> findByDeletedFalse();

    Optional<Package> findByIdAndDeletedFalse(Long id);

    Package findFirstByDeletedFalseOrderByIdDesc();
}
