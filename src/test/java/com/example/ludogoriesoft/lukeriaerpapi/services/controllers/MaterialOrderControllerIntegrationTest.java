package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.MaterialOrderController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.MaterialOrderService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = MaterialOrderController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = MaterialOrderController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class)
        }
)
class MaterialOrderControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterialOrderService materialOrderService;
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
    void testGetAllMaterialOrders() throws Exception {
        MaterialOrderDTO materialOrderDTO1 = new MaterialOrderDTO();
        materialOrderDTO1.setId(1L);
        MaterialOrderDTO materialOrderDTO2 = new MaterialOrderDTO();
        materialOrderDTO2.setId(2L);
        List<MaterialOrderDTO> materialOrderDTOList = Arrays.asList(materialOrderDTO1, materialOrderDTO2);

        when(materialOrderService.getAllMaterialOrders()).thenReturn(materialOrderDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/material-order")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
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
    void testGetMaterialOrderById() throws Exception {
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setId(1L);
        when(materialOrderService.getMaterialOrderById(anyLong())).thenReturn(materialOrderDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/material-order/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreateMaterialOrder() throws Exception {
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setId(1L);

        when(materialOrderService.createMaterialOrder(any(MaterialOrderDTO.class))).thenReturn(materialOrderDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/material-order")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"New MaterialOrder\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdateMaterialOrder() throws Exception {
        MaterialOrderDTO materialOrderDTO = new MaterialOrderDTO();
        materialOrderDTO.setId(1L);

        when(materialOrderService.updateMaterialOrder(anyLong(), any(MaterialOrderDTO.class))).thenReturn(materialOrderDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/material-order/{id}", 1)
                        .content("{\"id\": 1, \"name\": \"Updated MaterialOrder\"}")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeleteMaterialOrderById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/material-order/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Material Order with id: 1 has been deleted successfully!"));
    }

    @Test
    void testGetAllMaterialOrdersWhenNoMaterialOrderExist() throws Exception {
        when(materialOrderService.getAllMaterialOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/material-order")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    void testGetMaterialOrderByIdWhenMaterialOrderDoesNotExist() throws Exception {
        long materialOrderId = 1L;
        when(materialOrderService.getMaterialOrderById(materialOrderId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/material-order/{id}", materialOrderId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testShouldNotCreateMaterialOrderWithBlankMaterialOrderName() throws Exception {
        String blankMaterialOrderName = "";

        doThrow(new ValidationException())
                .when(materialOrderService).createMaterialOrder(any(MaterialOrderDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/material-order")
                        .content("{\"id\": 1, \"name\": \"" + blankMaterialOrderName + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    void testGetMaterialOrderWithInvalidId() throws Exception {
        Long invalidId = 100000L;

        when(materialOrderService.getMaterialOrderById(invalidId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/material-order/{id}", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testUpdateMaterialOrderWithInvalidData() throws Exception {
        String invalidData = "";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/material-order/{id}", 1)
                        .content("{\"id\": 1, \"name\": " + invalidData + "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateMaterialOrderWithInvalidIdShouldReturnNotFound() throws Exception {
        Long id = 1L;

        MaterialOrderDTO updatedMaterialOrder = new MaterialOrderDTO();

        when(materialOrderService.updateMaterialOrder(eq(id), any(MaterialOrderDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/material-order/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedMaterialOrder)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteMaterialOrderByIdWhenMaterialOrderDoesNotExist() throws Exception {
        long materialOrderId = 1L;
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(materialOrderService).deleteMaterialOrder(materialOrderId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/material-order/{id}", materialOrderId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testGetAllProductsByOrderId() throws Exception {
        Long orderId = 1L;
        List<MaterialOrderDTO> mockOrderProducts = new ArrayList<>();
        MaterialOrderDTO materialOrderDTO1 = new MaterialOrderDTO();
        materialOrderDTO1.setMaterialId(101L);
        materialOrderDTO1.setOrderedQuantity(5);
        mockOrderProducts.add(materialOrderDTO1);
        MaterialOrderDTO materialOrderDTO2 = new MaterialOrderDTO();
        materialOrderDTO2.setMaterialId(102L);
        materialOrderDTO2.setOrderedQuantity(10);
        mockOrderProducts.add(materialOrderDTO2);

        when(materialOrderService.getAllOrderProductsByOrderId(orderId)).thenReturn(mockOrderProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/material-order/products/{id}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].materialId").value(101L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderedQuantity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].materialId").value(102L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].orderedQuantity").value(10));
    }

    @Test
    void testAllAvailableProducts() throws Exception {
        MaterialOrderDTO order1 = new MaterialOrderDTO();
        MaterialOrderDTO order2 = new MaterialOrderDTO();
        List<MaterialOrderDTO> allOrderedProducts = Arrays.asList(order1, order2);

        List<MaterialOrderDTO> expectedMissingMaterials = Arrays.asList();

        when(materialOrderService.allOrderedProducts()).thenReturn(allOrderedProducts);
        when(materialOrderService.allMissingMaterials(allOrderedProducts)).thenReturn(expectedMissingMaterials);

        mockMvc.perform(get("/api/v1/material-order/all-missing-materials")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}