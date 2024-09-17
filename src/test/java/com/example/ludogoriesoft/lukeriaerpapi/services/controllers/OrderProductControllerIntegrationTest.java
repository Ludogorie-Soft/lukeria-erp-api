package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.OrderProductController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.OrderProductService;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = OrderProductController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = OrderProductController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class
                )
        }
)
class OrderProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SlackService slackService;

    @MockBean
    private OrderProductService orderProductService;

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
    void testGetAllOrderProducts() throws Exception {
        OrderProductDTO orderProductDTO1 = new OrderProductDTO();
        orderProductDTO1.setId(1L);
        OrderProductDTO orderProductDTO2 = new OrderProductDTO();
        orderProductDTO2.setId(2L);
        List<OrderProductDTO> orderProductDTOList = Arrays.asList(orderProductDTO1, orderProductDTO2);

        when(orderProductService.getAllOrderProducts()).thenReturn(orderProductDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderProduct")
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testGetOrderProductById() throws Exception {
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setId(1L);

        when(orderProductService.getOrderProductById(anyLong())).thenReturn(orderProductDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderProduct/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreateOrderProduct() throws Exception {
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setId(1L);

        when(orderProductService.createOrderProduct(any(OrderProductDTO.class))).thenReturn(orderProductDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdateOrderProduct() throws Exception {
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setId(1L);

        when(orderProductService.updateOrderProduct(anyLong(), any(OrderProductDTO.class))).thenReturn(orderProductDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orderProduct/{id}", 1)
                        .content("{\"id\": 1}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeleteOrderProductById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orderProduct/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order with id: 1 has been deleted successfully!"));
    }

    private List<OrderProductDTO> createSampleOrderProductDTOList() {
        OrderProductDTO orderProductDTO1 = new OrderProductDTO();
        orderProductDTO1.setId(null);
        orderProductDTO1.setNumber(1);
        orderProductDTO1.setOrderId(1L);
        orderProductDTO1.setPackageId(1L);
        orderProductDTO1.setSellingPrice(BigDecimal.valueOf(3.10));

        OrderProductDTO orderProductDTO2 = new OrderProductDTO();
        orderProductDTO2.setId(null);
        orderProductDTO2.setNumber(2);
        orderProductDTO2.setOrderId(1L);
        orderProductDTO2.setPackageId(1L);
        orderProductDTO2.setSellingPrice(BigDecimal.valueOf(3.20));

        OrderProductDTO orderProductDTO3 = new OrderProductDTO();
        orderProductDTO3.setId(null);
        orderProductDTO3.setNumber(3);
        orderProductDTO3.setOrderId(1L);
        orderProductDTO3.setPackageId(1L);
        orderProductDTO3.setSellingPrice(BigDecimal.valueOf(3.30));

        return List.of(orderProductDTO1, orderProductDTO2, orderProductDTO3);
    }

    @Test
    void testGetAllOrderProductsWhenNoOrderProductExist() throws Exception {
        when(orderProductService.getAllOrderProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    void testGetOrderProductByIdWhenOrderProductDoesNotExist() throws Exception {
        long orderProductId = 1L;
        when(orderProductService.getOrderProductById(orderProductId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderProduct/{id}", orderProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("")));
    }

    @Test
    void testShouldNotCreateOrderProductWithBlankOrderProductName() throws Exception {
        String blankOrderProductName = "";

        doThrow(new ValidationException())
                .when(orderProductService).createOrderProduct(any(OrderProductDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orderProduct")
                        .content("{\"id\": 1}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    void testGetOrderProductWithInvalidId() throws Exception {
        Long invalidId = 100000L;

        when(orderProductService.getOrderProductById(invalidId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orderProduct/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("")));
    }

    @Test
    void testUpdateOrderProductWithInvalidIdShouldReturnNotFound() throws Exception {
        Long id = 1L;

        OrderProductDTO updatedOrderProduct = new OrderProductDTO();

        when(orderProductService.updateOrderProduct(eq(id), any(OrderProductDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orderProduct/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedOrderProduct))
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOrderProductByIdWhenOrderProductDoesNotExist() throws Exception {
        long orderProductId = 1L;
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(orderProductService).deleteOrderProduct(orderProductId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orderProduct/{id}", orderProductId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("")));
    }
}
