package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.ClientUserController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ClientUserDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.ClientUserService;
import com.example.ludogoriesoft.lukeriaerpapi.slack.SlackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = ClientUserController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ClientUserController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class
                )
        }
)
class ClientUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SlackService slackService;

    @MockBean
    private ClientUserService clientUserService;

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
    public void testGetAllClientWithNoUser() throws Exception {
        ClientDTO client1 = new ClientDTO();
        client1.setId(1L);
        client1.setBusinessName("Client 1");

        ClientDTO client2 = new ClientDTO();
        client2.setId(2L);
        client2.setBusinessName("Client 2");

        List<ClientDTO> clientsWithNoUsers = Arrays.asList(client1, client2);

        // Mock the service method
        when(clientUserService.getAllClientsNotInClientUserHelper()).thenReturn(clientsWithNoUsers);

        // Act: Perform the GET request with the correct URL
        mockMvc.perform(get("/api/v1/client-user/clients/no-users")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON))
                // Assert: Verify the response status and content
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].businessName").value("Client 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].businessName").value("Client 2"));
    }
    @Test
    void testGetAllClientUsers() throws Exception {
        ClientUserDTO clientUser1 = new ClientUserDTO();
        clientUser1.setId(1L);
        ClientUserDTO clientUser2 = new ClientUserDTO();
        clientUser2.setId(2L);
        List<ClientUserDTO> clientUserList = Arrays.asList(clientUser1, clientUser2);

        when(clientUserService.getAllClientUsers()).thenReturn(clientUserList);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/client-user")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testGetClientUserById() throws Exception {
        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setId(1L);

        when(clientUserService.getClientUserById(anyLong())).thenReturn(clientUserDTO);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/client-user/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreateClientUser() throws Exception {
        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setId(1L);

        when(clientUserService.createClientUser(any(ClientUserDTO.class))).thenReturn(clientUserDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/client-user")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(clientUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdateClientUser() throws Exception {
        ClientUserDTO clientUserDTO = new ClientUserDTO();
        clientUserDTO.setId(1L);

        when(clientUserService.updateClientUser(anyLong(), any(ClientUserDTO.class))).thenReturn(clientUserDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/client-user/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(clientUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeleteClientUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/client-user/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testDeleteClientUserByIds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/client-user/deleteByUserAndClient/{userId}/{clientId}", 1,1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetClientUserByIdWhenNotFound() throws Exception {
        long clientUserId = 1L;
        when(clientUserService.getClientUserById(clientUserId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(get("/api/v1/client-user/{id}", clientUserId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
