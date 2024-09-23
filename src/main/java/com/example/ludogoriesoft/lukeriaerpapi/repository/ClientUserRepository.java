package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ClientUserRepository extends JpaRepository<ClientUser, Long> {
    List<ClientUser> findByDeletedFalse();

    Optional<ClientUser> findByIdAndDeletedFalse(Long id);

    Optional<ClientUser> findByClientIdAndUserIdAndDeletedFalse(Client client, User user);

}
