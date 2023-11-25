package com.example.ludogoriesoft.lukeriaerpapi.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigurationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUnauthenticatedAccessToProtectedEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
