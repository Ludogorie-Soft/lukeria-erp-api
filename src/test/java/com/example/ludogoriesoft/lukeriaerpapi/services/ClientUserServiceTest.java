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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientUserServiceTest {

    @Mock
    private ClientUserRepository clientUserRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    private ClientUserService clientUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        clientUserService = new ClientUserService(clientUserRepository, userRepository, clientRepository, modelMapper);
    }

    @Test
    void testGetAllClientUsers() {
        // Arrange
        List<ClientUser> clientUsers = new ArrayList<>();
        Client client = new Client();
        client.setId(1L);
        User user = new User();
        user.setId(1L);
        clientUsers.add(new ClientUser(1L, client, user, false));
        clientUsers.add(new ClientUser(2L, client, user, false));

        when(clientUserRepository.findByDeletedFalse()).thenReturn(clientUsers);

        ClientUserDTO clientUserDTO1 = new ClientUserDTO(1L, client.getId(), user.getId());
        ClientUserDTO clientUserDTO2 = new ClientUserDTO(2L, client.getId(), user.getId());

        when(modelMapper.map(clientUsers.get(0), ClientUserDTO.class)).thenReturn(clientUserDTO1);
        when(modelMapper.map(clientUsers.get(1), ClientUserDTO.class)).thenReturn(clientUserDTO2);

        // Act
        List<ClientUserDTO> result = clientUserService.getAllClientUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testGetClientUserById_Existing() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long clientUserId = 1L;
        Client client = new Client();
        client.setId(1L);
        User user = new User();
        user.setId(1L);
        ClientUser clientUser = new ClientUser(clientUserId, client, user, false);

        when(clientUserRepository.findByIdAndDeletedFalse(clientUserId)).thenReturn(Optional.of(clientUser));

        ClientUserDTO expectedDTO = new ClientUserDTO(clientUserId, client.getId(), user.getId());
        when(modelMapper.map(clientUser, ClientUserDTO.class)).thenReturn(expectedDTO);

        // Act
        ClientUserDTO result = clientUserService.getClientUserById(clientUserId);

        // Assert
        assertEquals(expectedDTO, result);
    }

    @Test
    void testGetClientUserById_NonExisting() {
        // Arrange
        Long clientUserId = 1L;
        when(clientUserRepository.findByIdAndDeletedFalse(clientUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> clientUserService.getClientUserById(clientUserId));
    }

    @Test
    void testCreateClientUser_Success() {
        Client client = new Client();
        client.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CUSTOMER);

        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setClientId(client.getId());
        clientUserDTO.setUserId(user.getId());

        ClientUser savedClientUser = new ClientUser();
        savedClientUser.setId(1L);
        savedClientUser.setClientId(client);
        savedClientUser.setUserId(user);
        savedClientUser.setDeleted(false);

        when(clientRepository.findByIdAndDeletedFalse(clientUserDTO.getClientId())).thenReturn(Optional.of(client));
        when(userRepository.findByIdAndDeletedFalse(clientUserDTO.getUserId())).thenReturn(Optional.of(user));
        when(modelMapper.map(clientUserDTO, ClientUser.class)).thenReturn(savedClientUser);
        when(clientUserRepository.save(any(ClientUser.class))).thenReturn(savedClientUser);
        when(modelMapper.map(savedClientUser, ClientUserDTO.class)).thenReturn(clientUserDTO);

        // Act
        ClientUserDTO result = clientUserService.createClientUser(clientUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getClientId());
        assertEquals(1L, result.getUserId());
    }
    @Test
    void testCreateClientUser_NotFoundException() {
        Client client = new Client();
        client.setId(1L);
        User user = new User();
        user.setRole(Role.CUSTOMER);

        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setClientId(client.getId());
        clientUserDTO.setUserId(user.getId());

        ClientUser savedClientUser = new ClientUser();
        savedClientUser.setId(1L);
        savedClientUser.setClientId(client);
        savedClientUser.setUserId(user);
        savedClientUser.setDeleted(false);

        when(clientRepository.findByIdAndDeletedFalse(clientUserDTO.getClientId())).thenReturn(Optional.of(client));
        when(userRepository.findByIdAndDeletedFalse(clientUserDTO.getUserId())).thenReturn(Optional.of(user));
        when(modelMapper.map(clientUserDTO, ClientUser.class)).thenReturn(savedClientUser);
        when(clientUserRepository.save(any(ClientUser.class))).thenReturn(savedClientUser);
        when(modelMapper.map(savedClientUser, ClientUserDTO.class)).thenReturn(clientUserDTO);

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> clientUserService.getClientUserById(null));

    }
    @Test
    void testCreateClientUser_RoleAdmin() {
        Client client = new Client();
        client.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setClientId(client.getId());
        clientUserDTO.setUserId(user.getId());

        ClientUser savedClientUser = new ClientUser();
        savedClientUser.setId(1L);
        savedClientUser.setClientId(client);
        savedClientUser.setUserId(user);
        savedClientUser.setDeleted(false);

        when(clientRepository.findByIdAndDeletedFalse(clientUserDTO.getClientId())).thenReturn(Optional.of(client));
        when(userRepository.findByIdAndDeletedFalse(clientUserDTO.getUserId())).thenReturn(Optional.of(user));
        when(modelMapper.map(clientUserDTO, ClientUser.class)).thenReturn(savedClientUser);
        when(clientUserRepository.save(any(ClientUser.class))).thenReturn(savedClientUser);
        when(modelMapper.map(savedClientUser, ClientUserDTO.class)).thenReturn(clientUserDTO);

        // Act
        assertThrows(ValidationException.class, () -> clientUserService.createClientUser(clientUserDTO));
    }
    @Test
    void testGetAllClientsNotInClientUserHelper() {
        Client clientWithUser = new Client();
        clientWithUser.setId(1L);
        Client clientWithoutUser = new Client();
        clientWithoutUser.setId(2L);

        ClientUser clientUser = new ClientUser();
        clientUser.setClientId(clientWithUser);

        when(clientRepository.findAll()).thenReturn(Arrays.asList(clientWithUser, clientWithoutUser));
        when(clientUserRepository.findAll()).thenReturn(Arrays.asList(clientUser));

        ClientDTO clientDTOWithUser = new ClientDTO();
        clientDTOWithUser.setId(1L);
        ClientDTO clientDTOWithoutUser = new ClientDTO();
        clientDTOWithoutUser.setId(2L);

        when(modelMapper.map(clientWithUser, ClientDTO.class)).thenReturn(clientDTOWithUser);
        when(modelMapper.map(clientWithoutUser, ClientDTO.class)).thenReturn(clientDTOWithoutUser);

        List<ClientDTO> result = clientUserService.getAllClientsNotInClientUserHelper();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId()); // Expecting the second client (without user)
    }

    @Test
    void testCreateClientUser_NonExistingClientOrUser() {
        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setClientId(1L);
        clientUserDTO.setUserId(1L);

        when(clientRepository.findById(clientUserDTO.getClientId())).thenReturn(Optional.empty());
        when(userRepository.findById(clientUserDTO.getUserId())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> clientUserService.createClientUser(clientUserDTO));
    }

    @Test
    void testDeleteClientUser_Existing() throws ChangeSetPersister.NotFoundException {
        Long clientUserId = 1L;
        ClientUser clientUser = mock(ClientUser.class);
        when(clientUserRepository.findByIdAndDeletedFalse(clientUserId)).thenReturn(Optional.of(clientUser));

        clientUserService.deleteClientUser(clientUserId);

        verify(clientUser).setDeleted(true);
        verify(clientUserRepository).save(clientUser);
    }

    @Test
    void testUpdateClientUser_Success() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long existingClientUserId = 1L;

        Client client = new Client();
        client.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CUSTOMER);

        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setId(1L);
        clientUserDTO.setClientId(1L);
        clientUserDTO.setUserId(1L);

        ClientUser existingClientUser = new ClientUser();
        existingClientUser.setId(existingClientUserId);
        existingClientUser.setClientId(client);
        existingClientUser.setUserId(user);

        ClientUser updatedClientUser= new ClientUser();
        updatedClientUser.setClientId(client);
        updatedClientUser.setUserId(user);

        when(clientUserRepository.findByIdAndDeletedFalse(existingClientUser.getId())).thenReturn(Optional.of(existingClientUser));
        when(clientRepository.findByIdAndDeletedFalse(clientUserDTO.getClientId())).thenReturn(Optional.of(client));
        when(userRepository.findByIdAndDeletedFalse(clientUserDTO.getUserId())).thenReturn(Optional.of(user));
        when(modelMapper.map(clientUserDTO, ClientUser.class)).thenReturn(updatedClientUser);
        when(clientUserRepository.save(any(ClientUser.class))).thenReturn(updatedClientUser);

        // Act
        ClientUserDTO result = clientUserService.updateClientUser(existingClientUserId, clientUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getClientId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void testUpdateClientUser_NonExisting() {
        // Arrange
        Long clientUserId = 1L;
        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setClientId(1L);
        clientUserDTO.setUserId(1L);

        when(clientUserRepository.findByIdAndDeletedFalse(clientUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> clientUserService.updateClientUser(clientUserId, clientUserDTO));
    }
}
