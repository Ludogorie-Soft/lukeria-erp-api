package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.MonthlyOrderController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MonthlyOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.MonthlyOrderService;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = MonthlyOrderController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = MonthlyOrderController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class
                )
        }
)
public class MonthlyOrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MonthlyOrderService monthlyOrderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMonthlyOrders() throws Exception {
        mockMvc.perform(get("/api/v1/monthlyOrder"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testGetMonthlyOrderById() throws Exception {
        Long id = 1L;
        mockMvc.perform(get("/api/v1/monthlyOrder/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateMonthlyOrder() throws Exception {
        MonthlyOrderDTO monthlyOrderDTO = new MonthlyOrderDTO();

        mockMvc.perform(post("/api/v1/monthlyOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(monthlyOrderDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateMonthlyOrder() throws Exception {
        Long id = 1L;
        MonthlyOrderDTO monthlyOrderDTO = new MonthlyOrderDTO();
        mockMvc.perform(put("/api/v1/monthlyOrder/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(monthlyOrderDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMonthlyOrder() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/v1/monthlyOrder/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void testFindFirstByOrderByIdDesc() throws Exception {
        mockMvc.perform(get("/api/v1/monthlyOrder/findLastMonthlyOrder"))
                .andExpect(status().isOk());
    }
}
