package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.ShoppingCartController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartItemDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ShoppingCartDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.ShoppingCartService;
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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = ShoppingCartController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ShoppingCartController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class)
        }
)
class ShoppingCartControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

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
    void testAddToCart() throws Exception {
        doNothing().when(shoppingCartService).addToCart(anyLong(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shoppingCart/addToCart")
                        .param("productId", "1")
                        .param("quantity", "2")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddToCartWithInvalidProductId() throws Exception {
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(shoppingCartService).addToCart(anyLong(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shoppingCart/addToCart")
                        .param("productId", "999")
                        .param("quantity", "2")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testShowCart() throws Exception {
        // Arrange
        CartItemDTO cartItemDTO1 = new CartItemDTO();
        cartItemDTO1.setId(1L);

        CartItemDTO cartItemDTO2 = new CartItemDTO();
        cartItemDTO2.setId(2L);

        List<CartItemDTO> cartItemDTOList = List.of(cartItemDTO1, cartItemDTO2);

        when(shoppingCartService.showCart()).thenReturn(cartItemDTOList);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shoppingCart/showCart")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }


    @Test
    void testShowCartWhenNotFound() throws Exception {
        when(shoppingCartService.showCart()).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shoppingCart/showCart")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveCartItem() throws Exception {
        doNothing().when(shoppingCartService).removeCartItem(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shoppingCart/removeCartItem")
                        .param("cartItemId", "1")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveCartItemWithInvalidId() throws Exception {
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(shoppingCartService).removeCartItem(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shoppingCart/removeCartItem")
                        .param("cartItemId", "999")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
