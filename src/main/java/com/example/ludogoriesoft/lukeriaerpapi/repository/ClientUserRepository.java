package com.example.ludogoriesoft.lukeriaerpapi.repository;

import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import java.util.List;
import java.util.Optional;

import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientUserRepository extends JpaRepository<ClientUser, Long> {
  List<ClientUser> findByDeletedFalse();

  Optional<ClientUser> findByIdAndDeletedFalse(Long id);

  Optional<ClientUser> findByClientIdAndUserIdAndDeletedFalse(Client client, User user);

  @Query("SELECT cu FROM ClientUser cu WHERE cu.user.id = :userId AND cu.client.id = :clientId")
  ClientUser findByUserIdAndClientId(
      @Param("userId") Long userId, @Param("clientId") Long clientId);

  Optional<ClientUser> findByUserIdAndDeletedFalse(Long userId);
}
