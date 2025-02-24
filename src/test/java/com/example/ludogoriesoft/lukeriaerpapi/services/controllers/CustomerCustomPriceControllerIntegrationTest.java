package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.CustomerCustomPriceController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.CustomerCustomPriceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.services.CustomerCustomPriceService;
import com.example.ludogoriesoft.lukeriaerpapi.slack.SlackService;
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
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = CustomerCustomPriceController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = CustomerCustomPriceController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class)
        }
)
public class CustomerCustomPriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SlackService slackService;

    @MockBean
    private CustomerCustomPriceService customerCustomPriceService;

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
    void testGetAllCustomPrices() throws Exception {
        CustomerCustomPriceDTO price1 = new CustomerCustomPriceDTO();
        price1.setId(1L);
        price1.setClientId(1L);
        price1.setProductId(1L);
        price1.setPrice(BigDecimal.valueOf(100.0));

        CustomerCustomPriceDTO price2 = new CustomerCustomPriceDTO();
        price2.setId(2L);
        price2.setClientId(1L);
        price2.setProductId(2L);
        price2.setPrice(BigDecimal.valueOf(200.0));

        List<CustomerCustomPriceDTO> prices = Arrays.asList(price1, price2);

        when(customerCustomPriceService.getAllCustomPrices()).thenReturn(prices);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customerCustomPrice")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].price").value(200.0));
    }
    @Test
    void testGetAllProductsWithCustomPriceForClient() throws Exception {
        Long clientId = 1L;

        CustomerCustomPriceDTO price1 = new CustomerCustomPriceDTO(1L, clientId, 1L, BigDecimal.valueOf(100.0));
        CustomerCustomPriceDTO price2 = new CustomerCustomPriceDTO(2L, clientId, 2L, BigDecimal.valueOf(200.0));

        List<CustomerCustomPriceDTO> prices = Arrays.asList(price1, price2);

        when(customerCustomPriceService.allProductWithCustomPriceForClient(clientId)).thenReturn(prices);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customerCustomPrice/allForClient/{id}", clientId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].price").value(200.0));
    }


    @Test
    void testCreateCustomPriceForCustomer() throws Exception {
        CustomerCustomPriceDTO newPrice = new CustomerCustomPriceDTO();
        newPrice.setId(1L);
        newPrice.setClientId(1L);
        newPrice.setProductId(1L);
        newPrice.setPrice(BigDecimal.valueOf(150.0));

        when(customerCustomPriceService.create(any(CustomerCustomPriceDTO.class))).thenReturn(newPrice);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customerCustomPrice")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newPrice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(150.0));
    }

    @Test
    void testUpdateCustomPrice() throws Exception {
        CustomerCustomPriceDTO updatedPrice = new CustomerCustomPriceDTO();
        updatedPrice.setId(1L);
        updatedPrice.setClientId(1L); // Client ID
        updatedPrice.setProductId(1L); // Product ID
        updatedPrice.setPrice(BigDecimal.valueOf(175.0));

        // Mock the update method

        when(customerCustomPriceService.update(any(CustomerCustomPriceDTO.class))).thenReturn(updatedPrice);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customerCustomPrice/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedPrice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(175.0));
    }

    @Test
    void testDeleteCustomPrice() throws Exception {
        CustomerCustomPriceDTO deletedPrice = new CustomerCustomPriceDTO();
        deletedPrice.setId(1L);
        deletedPrice.setClientId(1L);
        deletedPrice.setProductId(1L);
        deletedPrice.setPrice(BigDecimal.valueOf(100.0));

        when(customerCustomPriceService.delete(anyLong(), anyLong())).thenReturn(deletedPrice);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customerCustomPrice/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .param("clientId", "1")
                        .param("productId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void testGetAllCustomPricesWhenNoneExist() throws Exception {
        when(customerCustomPriceService.getAllCustomPrices()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customerCustomPrice")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testCreateCustomPriceWhenNotFound() throws Exception {
        CustomerCustomPriceDTO priceDTO = new CustomerCustomPriceDTO();
        priceDTO.setId(1L);
        priceDTO.setClientId(1L);
        priceDTO.setProductId(1L);
        priceDTO.setPrice(BigDecimal.valueOf(100.0));

        when(customerCustomPriceService.create(any(CustomerCustomPriceDTO.class))).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customerCustomPrice")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(priceDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCustomPriceWhenNotFound() throws Exception {
        CustomerCustomPriceDTO priceDTO = new CustomerCustomPriceDTO();
        priceDTO.setId(1L);
        priceDTO.setClientId(1L);
        priceDTO.setProductId(1L);
        priceDTO.setPrice(BigDecimal.valueOf(175.0));

        when(customerCustomPriceService.update(any(CustomerCustomPriceDTO.class))).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customerCustomPrice/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(priceDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCustomPriceWhenNotFound() throws Exception {
        when(customerCustomPriceService.delete(anyLong(), anyLong())).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customerCustomPrice/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .param("clientId", "1")
                        .param("productId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetCustomPriceByClientAndProduct_ShouldReturnCustomPrice_WhenFound() throws Exception {
        Long clientId = 1L;
        Long productId = 1L;

        CustomerCustomPriceDTO priceDTO = new CustomerCustomPriceDTO();
        priceDTO.setId(1L);
        priceDTO.setClientId(clientId);
        priceDTO.setProductId(productId);
        priceDTO.setPrice(BigDecimal.valueOf(100.0));

        when(customerCustomPriceService.findByClientAndProduct(clientId, productId)).thenReturn(priceDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customerCustomPrice/findByClientAndProduct")
                        .param("clientId", clientId.toString())
                        .param("productId", productId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void testGetCustomPriceByClientAndProduct_ShouldReturnNotFound_WhenClientNotFound() throws Exception {
        Long clientId = 1L;
        Long productId = 1L;

        when(customerCustomPriceService.findByClientAndProduct(clientId, productId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customerCustomPrice/findByClientAndProduct")
                        .param("clientId", clientId.toString())
                        .param("productId", productId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCustomPriceByClientAndProduct_ShouldReturnNotFound_WhenProductNotFound() throws Exception {
        Long clientId = 1L;
        Long productId = 1L;

        when(customerCustomPriceService.findByClientAndProduct(clientId, productId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customerCustomPrice/findByClientAndProduct")
                        .param("clientId", clientId.toString())
                        .param("productId", productId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCustomPriceByClientAndProduct_ShouldReturnNotFound_WhenCustomPriceNotFound() throws Exception {
        Long clientId = 1L;
        Long productId = 1L;

        when(customerCustomPriceService.findByClientAndProduct(clientId, productId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customerCustomPrice/findByClientAndProduct")
                        .param("clientId", clientId.toString())
                        .param("productId", productId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
