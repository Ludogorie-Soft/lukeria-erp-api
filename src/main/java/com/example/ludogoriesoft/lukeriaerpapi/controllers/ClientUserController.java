package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.services.ClientUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/client-user")
public class ClientUserController {

    private ClientUserService clientUserService;

    @PostMapping
    public ResponseEntity<ClientUserDTO> createClientUser(@RequestBody ClientUserDTO clientUserDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(clientUserService.createClientUser(clientUserDTO));
    }
    @GetMapping
    public List<ClientUserDTO> getAllClientUsers(@RequestHeader("Authorization") String auth) {
        return clientUserService.getAllClientUsers();
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClientUserDTO> getClientUser(@PathVariable Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(clientUserService.getClientUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientUserDTO> updateClientUser(@PathVariable Long id, @RequestBody ClientUserDTO clientUserDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(clientUserService.updateClientUser(id, clientUserDTO));
    }

    @DeleteMapping("/{id}")
    public void deleteClientUser(@PathVariable Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        clientUserService.deleteClientUser(id);
    }
}

