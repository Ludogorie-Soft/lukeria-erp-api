package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.MonthlyOrderProductController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.MonthlyOrderProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = MonthlyOrderProductController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = MonthlyOrderProductController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class
                )
        }
)
class MonthlyOrderProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MonthlyOrderProductService monthlyOrderProductService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMonthlyProductOrders() throws Exception {
        mockMvc.perform(get("/api/v1/monthlyOrderProduct"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMonthlyOrderProductById() throws Exception {
        Long id = 1L;
        mockMvc.perform(get("/api/v1/monthlyOrderProduct/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateMonthlyProductOrder() throws Exception {
        MonthlyOrderProductDTO monthlyOrderProductDTO = new MonthlyOrderProductDTO();

        mockMvc.perform(post("/api/v1/monthlyOrderProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(monthlyOrderProductDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateMonthlyProductOrder() throws Exception {
        Long id = 1L;

        MonthlyOrderProductDTO monthlyOrderProductDTO = new MonthlyOrderProductDTO();

        mockMvc.perform(put("/api/v1/monthlyOrderProduct/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(monthlyOrderProductDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMonthlyProductOrder() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/v1/monthlyOrderProduct/{id}", id))
                .andExpect(status().isOk());
    }
}
