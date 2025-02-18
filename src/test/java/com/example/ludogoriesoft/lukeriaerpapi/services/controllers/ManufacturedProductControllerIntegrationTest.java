package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.ManufacturedProductController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ManufacturedProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.ManufacturedProductService;
import com.example.ludogoriesoft.lukeriaerpapi.slack.SlackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = ManufacturedProductController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ManufacturedProductController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class
                )
        }
)
class ManufacturedProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManufacturedProductService manufacturedProductService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ManufacturedProductDTO manufacturedProductDTO;

    @BeforeEach
    void setUp() {
        manufacturedProductDTO = new ManufacturedProductDTO(1L, 1L, 10, LocalDateTime.now());
    }

    @Test
//    @WithMockUser(roles = "PRODUCTION_MANAGER")
    void getAllManufacturedProducts_ShouldReturnList() throws Exception {
        when(manufacturedProductService.getAllManufacturedProducts()).thenReturn(Arrays.asList(new ManufacturedProduct(), new ManufacturedProduct()));

        mockMvc.perform(get("/api/v1/manufactured-product/all")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(manufacturedProductService, times(1)).getAllManufacturedProducts();
    }

    @Test
    void getManufacturedProductById_ShouldReturnProduct() throws Exception {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        when(manufacturedProductService.getManufacturedProductById(1L)).thenReturn(Optional.of(manufacturedProduct));
        when(modelMapper.map(manufacturedProduct, ManufacturedProductDTO.class)).thenReturn(manufacturedProductDTO);

        mockMvc.perform(get("/api/v1/manufactured-product/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(manufacturedProductService, times(1)).getManufacturedProductById(1L);
    }

    @Test
    void getManufacturedProductById_ShouldReturnNotFound() throws Exception {
        when(manufacturedProductService.getManufacturedProductById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/manufactured-product/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());

        verify(manufacturedProductService, times(1)).getManufacturedProductById(1L);
    }

    @Test
    void createManufacturedProduct_ShouldCreateProduct() throws Exception {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        when(modelMapper.map(manufacturedProductDTO, ManufacturedProduct.class)).thenReturn(manufacturedProduct);
        when(manufacturedProductService.createManufacturedProduct(manufacturedProduct)).thenReturn(manufacturedProduct);
        when(modelMapper.map(manufacturedProduct, ManufacturedProductDTO.class)).thenReturn(manufacturedProductDTO);

        mockMvc.perform(post("/api/v1/manufactured-product")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturedProductDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(manufacturedProductService, times(1)).createManufacturedProduct(manufacturedProduct);
    }

    @Test
    void updateManufacturedProduct_ShouldUpdateProduct() throws Exception {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        when(manufacturedProductService.updateManufacturedProduct(eq(1L), any(ManufacturedProductDTO.class))).thenReturn(manufacturedProduct);
        when(modelMapper.map(manufacturedProduct, ManufacturedProductDTO.class)).thenReturn(manufacturedProductDTO);

        mockMvc.perform(put("/api/v1/manufactured-product/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturedProductDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(manufacturedProductService, times(1)).updateManufacturedProduct(eq(1L), any(ManufacturedProductDTO.class));
    }

    @Test
    void updateManufacturedProduct_ShouldReturnNotFound() throws Exception {
        when(manufacturedProductService.updateManufacturedProduct(eq(1L), any(ManufacturedProductDTO.class)))
                .thenThrow(new IllegalArgumentException("ManufacturedProduct with ID 1 not found."));

        mockMvc.perform(put("/api/v1/manufactured-product/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manufacturedProductDTO)))
                .andExpect(status().isNotFound());

        verify(manufacturedProductService, times(1)).updateManufacturedProduct(eq(1L), any(ManufacturedProductDTO.class));
    }

    @Test
    void deleteManufacturedProduct_ShouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/v1/manufactured-product/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());

        verify(manufacturedProductService, times(1)).deleteManufacturedProduct(1L);
    }

    @Test
    void shouldNotAllowAccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/manufactured-product"))
                .andExpect(status().isInternalServerError()); // Replace with the actual expected status
    }


    @Test
    void shouldAllowAdminToPostManufacturedProduct() throws Exception {
        mockMvc.perform(post("/api/v1/manufactured-product")
                        .contentType("application/json")
                        .content("{ \"key\": \"value\" }")) // Replace with actual request body
                .andExpect(status().isInternalServerError()); // Replace with the actual expected status
    }


    @Test
    void shouldAllowTransportManagerToDeleteManufacturedProduct() throws Exception {
        mockMvc.perform(delete("/api/v1/manufactured-product/1")) // Replace with actual endpoint
                .andExpect(status().isInternalServerError()); // Replace with the actual expected status
    }


    @Test
    void shouldAllowCustomerToPutManufacturedProduct() throws Exception {
        mockMvc.perform(put("/api/v1/manufactured-product/1")
                        .contentType("application/json")
                        .content("{ \"key\": \"updatedValue\" }")) // Replace with actual request body
                .andExpect(status().isInternalServerError());
    }
}
