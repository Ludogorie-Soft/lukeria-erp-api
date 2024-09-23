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
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .map(clientUser -> modelMapper.map(clientUser, ClientUserDTO.class))
                .collect(Collectors.toList());
    }


    public ClientUserDTO getClientUserById(Long id) throws ChangeSetPersister.NotFoundException {
        ClientUser clientUser = clientUserRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(clientUser, ClientUserDTO.class);
    }


    public ClientUserDTO createClientUser(ClientUserDTO clientUserDTO) throws ChangeSetPersister.NotFoundException {
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

    private void validations(ClientUserDTO clientUserDTO) throws ChangeSetPersister.NotFoundException {
        User user = userRepository.findByIdAndDeletedFalse(clientUserDTO.getUserId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Client client = clientRepository.findByIdAndDeletedFalse(clientUserDTO.getClientId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (clientUserDTO.getClientId() == null || clientUserDTO.getClientId() == 0 ||
                clientUserDTO.getUserId() == null || clientUserDTO.getUserId() == 0) {
            throw new ValidationException("Client ID and User ID are required and must be greater than 0!");
        }
        if (!user.getRole().equals(Role.CUSTOMER)){
            throw new ValidationException("User is not customer!");
        }
        Optional<ClientUser>optionalClientUser = clientUserRepository.findByClientIdAndUserIdAndDeletedFalse(client, user);
        if (optionalClientUser.isPresent()){
            throw new ValidationException("Client User already exist");
        }
    }
    public void deleteClientUser(Long id) throws ChangeSetPersister.NotFoundException {
        ClientUser clientUser = clientUserRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        clientUser.setDeleted(true);
        clientUserRepository.save(clientUser);
    }
}
