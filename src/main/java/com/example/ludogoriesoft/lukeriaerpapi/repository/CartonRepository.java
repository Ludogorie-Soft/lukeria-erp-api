package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartonRepository extends JpaRepository<Carton, Long> {
    List<Carton> findByDeletedFalse();

    Optional<Carton> findByIdAndDeletedFalse(Long id);
}
