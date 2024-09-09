package com.example.ludogoriesoft.lukeriaerpapi.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.UserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.auth.AuthenticationResponse;
import com.example.ludogoriesoft.lukeriaerpapi.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO user, @RequestHeader("Authorization") String auth) {
        UserDTO createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/authenticated/{id}")
    public ResponseEntity<AuthenticationResponse> updateAuthenticatedUser(@PathVariable("id") Long id, @Valid @RequestBody UserDTO userDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(userService.updateAuthenticateUser(id, userDTO));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDTO userDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException{
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.ok("User with id: " + id + " has been deleted successfully!");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> findAuthenticatedUser(@RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(userService.findAuthenticatedUser());
    }
    @GetMapping("/ifPassMatch")
    public boolean ifPassMatch(@RequestParam String password, @RequestHeader("Authorization") String auth ) {
        return userService.ifPasswordMatch(password);
    }
    @PutMapping("/change-pass")
    public boolean changePassword(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String auth) throws ChangeSetPersister.NotFoundException {
        return userService.updatePassword(userDTO);
    }
}
