package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.ClientController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.ClientService;
import com.example.ludogoriesoft.lukeriaerpapi.slack.SlackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = ClientController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ClientController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class)

        }
)
class ClientControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;
    @MockBean
    private SlackService slackService;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllClients() throws Exception {
        ClientDTO clientDTO1 = new ClientDTO();
        clientDTO1.setId(1L);
        clientDTO1.setBusinessName("Client 1");
        ClientDTO clientDTO2 = new ClientDTO();
        clientDTO2.setId(2L);
        clientDTO2.setBusinessName("Client 2");
        List<ClientDTO> clientDTOList = Arrays.asList(clientDTO1, clientDTO2);

        when(clientService.getAllClients()).thenReturn(clientDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].businessName").value("Client 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].businessName").value("Client 2"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }
    @Test
    @WithMockUser(roles = "USER")
    void testGetAllClientsWithUserRole() throws Exception {
        ClientDTO clientDTO1 = new ClientDTO();
        clientDTO1.setId(1L);
        clientDTO1.setBusinessName("Client 1");
        ClientDTO clientDTO2 = new ClientDTO();
        clientDTO2.setId(2L);
        clientDTO2.setBusinessName("Client 2");
        List<ClientDTO> clientDTOList = Arrays.asList(clientDTO1, clientDTO2);

        when(clientService.getAllClients()).thenReturn(clientDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].businessName").value("Client 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].businessName").value("Client 2"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testGetClientById() throws Exception {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setBusinessName("Client 1");

        when(clientService.getClientById(anyLong())).thenReturn(clientDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("Client 1"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }
    @Test
    @WithMockUser(roles = "USER")
    void testGetClientByIdWithUSerRole() throws Exception {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setBusinessName("Client 1");

        when(clientService.getClientById(anyLong())).thenReturn(clientDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("Client 1"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreateClient() throws Exception {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setBusinessName("New Client");

        when(clientService.createClient(any(ClientDTO.class))).thenReturn(clientDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/client")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"businessName\": \"New Client\"}")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("New Client"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdateClient() throws Exception {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setBusinessName("Updated Client");

        when(clientService.updateClient(anyLong(), any(ClientDTO.class))).thenReturn(clientDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/client/{id}", 1)
                        .content("{\"id\": 1, \"businessName\": \"Updated Client\"}")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("Updated Client"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeleteClientById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/client/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Client with id: 1 has been deleted successfully!"));
    }

    @Test
    void testGetAllClientsWhenNoClientExist() throws Exception {
        when(clientService.getAllClients()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    void testGetClientByIdWhenClientDoesNotExist() throws Exception {
        long clientId = 1L;
        when(clientService.getClientById(clientId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client/{id}", clientId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testShouldNotCreateClientWithBlankClientName() throws Exception {
        String blankClientName = "";

        doThrow(new ValidationException())
                .when(clientService).createClient(any(ClientDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/client")
                        .content("{\"id\": 1, \"businessName\": \"" + blankClientName + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    void testGetClientWithInvalidId() throws Exception {
        Long invalidId = 100000L;

        when(clientService.getClientById(invalidId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/client/{id}", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testUpdateClientWithInvalidData() throws Exception {
        String invalidData = "";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/client/{id}", 1)
                        .content("{\"id\": 1, \"businessName\": " + invalidData + "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateClientWithInvalidIdShouldReturnNotFound() throws Exception {
        Long id = 1L;

        ClientDTO updatedClient = new ClientDTO();

        when(clientService.updateClient(eq(id), any(ClientDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/client/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedClient))
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteClientByIdWhenClientDoesNotExist() throws Exception {
        long clientId = 1L;
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(clientService).deleteClient(clientId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/client/{id}", clientId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }
}
