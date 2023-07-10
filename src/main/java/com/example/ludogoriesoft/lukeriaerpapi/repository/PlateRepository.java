package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlateRepository extends JpaRepository<Plate, Long> {
    List<Plate> findByDeletedFalse();
    Optional<Plate> findByIdAndDeletedFalse(Long id);
}
