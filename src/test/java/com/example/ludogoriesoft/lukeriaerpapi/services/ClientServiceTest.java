package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllClients() {
        Client client1 = new Client();
        client1.setId(1L);
        client1.setBusinessName("Client 1");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setBusinessName("Client 2");

        List<Client> mockClients = Arrays.asList(client1, client2);
        when(clientRepository.findByDeletedFalse()).thenReturn(mockClients);

        ClientDTO clientDTO1 = new ClientDTO();
        clientDTO1.setId(1L);
        clientDTO1.setBusinessName("Client 1");

        ClientDTO clientDTO2 = new ClientDTO();
        clientDTO2.setId(2L);
        clientDTO2.setBusinessName("Client 2");

        when(modelMapper.map(client1, ClientDTO.class)).thenReturn(clientDTO1);
        when(modelMapper.map(client2, ClientDTO.class)).thenReturn(clientDTO2);

        List<ClientDTO> result = clientService.getAllClients();

        assertEquals(mockClients.size(), result.size());
        assertEquals(mockClients.get(0).getBusinessName(), result.get(0).getBusinessName());
        assertEquals(mockClients.get(1).getBusinessName(), result.get(1).getBusinessName());

        verify(clientRepository, times(1)).findByDeletedFalse();

        verify(modelMapper, times(mockClients.size())).map(any(Client.class), eq(ClientDTO.class));
    }

    @Test
    void testGetClientById_ExistingId() throws ChangeSetPersister.NotFoundException {
        Client client = new Client();
        client.setId(1L);
        client.setBusinessName("Client 1");

        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setBusinessName("Client 1");

        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.getClientById(1L);

        assertEquals(clientDTO.getId(), result.getId());
        assertEquals(clientDTO.getBusinessName(), result.getBusinessName());

        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(modelMapper, times(1)).map(client, ClientDTO.class);
    }

    @Test
    void testGetClientById_NonExistingId() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> clientService.getClientById(1L));

        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testCreateClient_InvalidClientDTO_IsNumEIKMissing() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("business name");
        clientDTO.setAddress("address 1");
        clientDTO.setIdNumEIK("1");
        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("The EIK number should contain at least 5 numbers!", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testCreateClient_InvalidClientDTO_BusinessNameMissing() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setIsBulgarianClient("true");
        clientDTO.setEnglishBusinessName("en name");
        clientDTO.setIdNumEIK("12345");
        clientDTO.setAddress("address");
        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("Business name is required!", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testCreateClient_InvalidClientDTO_AddressMissing() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("business name");
        clientDTO.setIdNumEIK("12345");
        clientDTO.setIsBulgarianClient("true");

        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("Address is required!", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testCreateClient_InvalidClientDTO_EnglishAddressMissing() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("business name");
        clientDTO.setEnglishBusinessName("en name");
        clientDTO.setIdNumEIK("12345");
        clientDTO.setAddress("address");
        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("Address in english is required!", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testCreateClient_InvalidClientDTO_EnglishNameError() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("business name");
        clientDTO.setEnglishBusinessName("эяаэяаэ");
        clientDTO.setEnglishAddress("name");
        clientDTO.setIdNumEIK("12345");
        clientDTO.setAddress("address");
        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("English name can contain only letters in English", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testCreateClient_InvalidClientDTO_EnglishAddressError() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("business name");
        clientDTO.setEnglishBusinessName("name");
        clientDTO.setEnglishAddress("иэх");
        clientDTO.setIdNumEIK("12345");
        clientDTO.setEnglishMol("Jenna");
        clientDTO.setAddress("address");
        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("English address can contain only letters in English", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testCreateClient_InvalidClientDTO_EnglishMolError() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("business name");
        clientDTO.setEnglishBusinessName("name");
        clientDTO.setEnglishAddress("иэх");
        clientDTO.setIdNumEIK("12345");
        clientDTO.setEnglishMol("Няма име");
        clientDTO.setAddress("address");
        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("English MOL can contain only letters in English", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testCreateClient_InvalidClientDTO_EnglishNameMissing() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("business name");
        clientDTO.setEnglishAddress("address");
        clientDTO.setIdNumEIK("12345");
        clientDTO.setAddress("address");
        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(clientDTO));
        assertEquals("Business name is english is required!", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testUpdateClient_MissingBusinessName() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setIdNumEIK("123456");
        clientDTO.setAddress("address");

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setBusinessName("Existing Client");
        existingClient.setIdNumEIK("123456");
        existingClient.setAddress("address");

        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingClient));
        assertThrows(ValidationException.class, () -> clientService.updateClient(1L, clientDTO));
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdateClient_MissingNumEIK() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("name");
        clientDTO.setAddress("address");
        clientDTO.setIdNumEIK("1");

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setBusinessName("Existing Client");
        existingClient.setIdNumEIK("123456");
        existingClient.setAddress("address");

        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingClient));
        assertThrows(ValidationException.class, () -> clientService.updateClient(1L, clientDTO));
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdateClient_MissingAddress() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("name");
        clientDTO.setIdNumEIK("123456");

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setBusinessName("Existing Client");
        existingClient.setIdNumEIK("123456");
        existingClient.setAddress("address");

        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingClient));
        assertThrows(ValidationException.class, () -> clientService.updateClient(1L, clientDTO));
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdateClient_MissingEnglishAddress() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("name");
        clientDTO.setIdNumEIK("123456");
        clientDTO.setEnglishBusinessName("en name");

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setBusinessName("Existing Client");
        existingClient.setIdNumEIK("123456");
        existingClient.setAddress("address");

        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingClient));
        assertThrows(ValidationException.class, () -> clientService.updateClient(1L, clientDTO));
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdateClient_MissingEnglishName() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("name");
        clientDTO.setIdNumEIK("123456");

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setBusinessName("Existing Client");
        existingClient.setIdNumEIK("123456");
        existingClient.setAddress("address");

        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingClient));
        assertThrows(ValidationException.class, () -> clientService.updateClient(1L, clientDTO));
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testDeleteClient_ExistingId() throws ChangeSetPersister.NotFoundException {
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setDeleted(false);
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingClient));
        clientService.deleteClient(1L);
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testDeleteClient_NonExistingId() {
        when(clientRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> clientService.deleteClient(1L));
        verify(clientRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testUpdateClient_ValidClient() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long clientId = 1L;
        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setBusinessName("Client 1");
        existingClient.setIdNumEIK("123456");
        existingClient.setAddress("address");

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("Updated Client 1");
        clientDTO.setIdNumEIK("12345678");
        clientDTO.setAddress("updates address");
        clientDTO.setEnglishAddress("address");
        clientDTO.setEnglishBusinessName("en name");
        clientDTO.setEnglishMol("MOL");

        when(clientRepository.findByIdAndDeletedFalse(clientId)).thenReturn(Optional.of(existingClient));
        when(modelMapper.map(existingClient, ClientDTO.class)).thenReturn(clientDTO);

        // Act
        Client updatedClient = new Client();
        updatedClient.setId(clientId);
        when(clientRepository.save(existingClient)).thenReturn(updatedClient);
        ClientDTO result = clientService.updateClient(clientId, clientDTO);


        verify(clientRepository).save(existingClient);
    }

    @Test
    void testCreateClient_ValidClient() {
        // Arrange
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBusinessName("Updated Client 1");
        clientDTO.setIdNumEIK("12345678");
        clientDTO.setAddress("updates address");

        Client clientEntity = new Client();
        clientEntity.setBusinessName("Client 1");
        clientEntity.setIdNumEIK("Size 1");
        clientEntity.setAddress("address");
        clientDTO.setEnglishAddress("address");
        clientDTO.setEnglishBusinessName("en name");
        clientDTO.setEnglishMol("MOL");

        when(clientRepository.save(any(Client.class))).thenReturn(clientEntity);
        when(modelMapper.map(clientDTO, Client.class)).thenReturn(clientEntity);
        when(modelMapper.map(clientEntity, ClientDTO.class)).thenReturn(clientDTO);

        // Act
        ClientDTO result = clientService.createClient(clientDTO);

        // Assert
        assertEquals(clientDTO.getBusinessName(), result.getBusinessName());
        assertEquals(clientDTO.getAddress(), result.getAddress());
        assertEquals(clientDTO.getIdNumEIK(), result.getIdNumEIK());
        assertEquals(clientDTO.getBusinessName(), result.getBusinessName());

        // Verify that clientRepository.save() is called with the expected Client object
        verify(clientRepository).save(clientEntity);
    }


}