package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.Role;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.ClientUser;
import com.example.ludogoriesoft.lukeriaerpapi.models.User;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientUserRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientUserService {
    private final ClientUserRepository clientUserRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public List<ClientUserDTO> getAllClientUsers() {
        List<ClientUser> clientUsers = clientUserRepository.findByDeletedFalse();
        return clientUsers.stream()
                .map(clientUser -> modelMapper.map(clientUser, ClientUserDTO.class)).toList();
    }
    public List<ClientDTO> getAllClientsNotInClientUserHelper() {
        List<Client> allClients = clientRepository.findAll();
        List<ClientUser> clientUsers = clientUserRepository.findAll();

        return allClients.stream()
                .filter(client -> clientUsers.stream()
                        .noneMatch(clientUser -> clientUser.getClient().equals(client)))
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .toList();
    }

    public ClientUserDTO getClientUserById(Long id) throws ChangeSetPersister.NotFoundException {
        ClientUser clientUser = clientUserRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(clientUser, ClientUserDTO.class);
    }


    public ClientUserDTO createClientUser(ClientUserDTO clientUserDTO) {
        validations(clientUserDTO);
        ClientUser clientUserEntity = clientUserRepository.save(modelMapper.map(clientUserDTO, ClientUser.class));
        return modelMapper.map(clientUserEntity, ClientUserDTO.class);
    }

    public ClientUserDTO updateClientUser(Long id, ClientUserDTO clientUserDTO) throws ChangeSetPersister.NotFoundException {
        ClientUser existingClientUser = clientUserRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        validations(clientUserDTO);
        clientUserDTO.setId(existingClientUser.getId());
        clientUserRepository.save(modelMapper.map(clientUserDTO, ClientUser.class));
        return clientUserDTO;
    }

    public void deleteClientUser(Long id) throws ChangeSetPersister.NotFoundException {
        ClientUser clientUser = clientUserRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        clientUser.setDeleted(true);
        clientUserRepository.save(clientUser);
    }
    public void deleteClientUser(Long userId,Long clientId) {
        ClientUser clientUserForDeleting=clientUserRepository.findByUserIdAndClientId(userId,clientId);
        clientUserRepository.delete(clientUserForDeleting);
    }

    private void validations(ClientUserDTO clientUserDTO) {
        User user = userRepository.findByIdAndDeletedFalse(clientUserDTO.getUserId())
                .orElseThrow(() -> new ValidationException("User not found or deleted"));
        Client client = clientRepository.findByIdAndDeletedFalse(clientUserDTO.getClientId())
                .orElseThrow(() -> new ValidationException("Client not found or deleted"));

        validateUserRole(user);
        checkIfClientUserExists(client, user);
    }

    private void validateUserRole(User user) {
        if (!user.getRole().equals(Role.CUSTOMER)) {
            throw new ValidationException("User is not a customer!");
        }
    }

    private void checkIfClientUserExists(Client client, User user) {
        Optional<ClientUser> optionalClientUser = clientUserRepository.findByClientIdAndUserIdAndDeletedFalse(client, user);
        if (optionalClientUser.isPresent()) {
            throw new ValidationException("Client User already exists");
        }
    }

}
