package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.ManufacturedProductController;
import com.example.ludogoriesoft.lukeriaerpapi.controllers.ProductController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ManufacturedProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.models.ManufacturedProduct;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.ManufacturedProductService;
import com.example.ludogoriesoft.lukeriaerpapi.slack.SlackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
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
    private ModelMapper modelMapper;  // Mocking ModelMapper
    @MockBean
    private ProductRepository productRepository;  // Mocking ModelMapper
    @MockBean
    private SlackService slackService;  // Mocking SlackService

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
    void testGetAllManufacturedProducts() throws Exception {
        ManufacturedProduct manufacturedProduct1 = new ManufacturedProduct();
        manufacturedProduct1.setId(1L);
        ManufacturedProduct manufacturedProduct2 = new ManufacturedProduct();
        manufacturedProduct2.setId(2L);

        // Setting up the behavior for the service mock
        when(manufacturedProductService.getAllManufacturedProducts()).thenReturn(Arrays.asList(manufacturedProduct1, manufacturedProduct2));

        // Mocking the modelMapper behavior
        when(modelMapper.map(manufacturedProduct1, ManufacturedProductDTO.class)).thenReturn(new ManufacturedProductDTO() {{ setId(1L); }});
        when(modelMapper.map(manufacturedProduct2, ManufacturedProductDTO.class)).thenReturn(new ManufacturedProductDTO() {{ setId(2L); }});

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/manufactured-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token"))
                .andExpect(status().isOk())
                .andReturn(); // Return the MvcResult

        // Assert that the MvcResult is not null
        Assertions.assertNotNull(mvcResult);

        // Assert that the response content is not null
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(responseContent);

        // Optionally, assert content length or specific fields in your response
        Assertions.assertFalse(responseContent.isEmpty(), "Response content should not be empty.");

        // Followed by specific JSON structure assertions
        // Expected JSON length, etc.
        Assertions.assertTrue(responseContent.contains("1"), "Response content should contain ID 1.");
    }


    @Test
    void testGetManufacturedProductById() throws Exception {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        manufacturedProduct.setId(1L);
        manufacturedProduct.setProduct(new Product()); // Assuming there's a Product field

        ManufacturedProductDTO manufacturedProductDTO = new ManufacturedProductDTO();
        manufacturedProductDTO.setId(1L);

        // Mocking the service's behavior to return the manufactured product
        when(manufacturedProductService.getManufacturedProductById(anyLong())).thenReturn(Optional.of(manufacturedProduct));
        // Mocking the modelMapper behavior
        when(modelMapper.map(manufacturedProduct, ManufacturedProductDTO.class)).thenReturn(manufacturedProductDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/manufactured-product/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        // Assert that the MvcResult is not null
        Assertions.assertNotNull(mvcResult);

        // Assert that the response content is not null or empty
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(responseContent);
        Assertions.assertFalse(responseContent.isEmpty(), "Response content should not be empty.");

        // Optionally, you can add additional assertions on the response
        // For example, you can check for other fields returned in the response
        Assertions.assertTrue(responseContent.contains("\"id\":1"), "Response content should contain ID 1.");
    }


    @Test
    void testCreateManufacturedProduct() throws Exception {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        manufacturedProduct.setId(1L);

        // Assuming there's a corresponding DTO class
        ManufacturedProductDTO manufacturedProductDTO = new ManufacturedProductDTO();
        manufacturedProductDTO.setId(1L);

        // Mocking the service's behavior to return the created manufactured product
        when(manufacturedProductService.createManufacturedProduct(any(ManufacturedProduct.class))).thenReturn(manufacturedProduct);
        // Mocking the modelMapper behavior to map the entity to DTO
        when(modelMapper.map(manufacturedProduct, ManufacturedProductDTO.class)).thenReturn(manufacturedProductDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/manufactured-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .content(asJsonString(manufacturedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        // Assert that the MvcResult is not null
        Assertions.assertNotNull(mvcResult);

        // Assert that the response content is not null or empty
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(responseContent);
        Assertions.assertFalse(responseContent.isEmpty(), "Response content should not be empty.");

        // Optionally, you can add additional assertions on the response
        Assertions.assertTrue(responseContent.contains("\"id\":1"), "Response content should contain ID 1.");
    }

    @Test
    void testUpdateManufacturedProduct() throws Exception {
        ManufacturedProduct manufacturedProduct = new ManufacturedProduct();
        manufacturedProduct.setId(1L);

        // Assuming there's a corresponding DTO class
        ManufacturedProductDTO manufacturedProductDTO = new ManufacturedProductDTO();
        manufacturedProductDTO.setId(1L);

        // Mocking the service's behavior to return the updated manufactured product
        when(manufacturedProductService.updateManufacturedProduct(anyLong(), any(ManufacturedProduct.class))).thenReturn(manufacturedProduct);
        // Mocking the modelMapper behavior to map the entity to DTO
        when(modelMapper.map(manufacturedProduct, ManufacturedProductDTO.class)).thenReturn(manufacturedProductDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/manufactured-product/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token")
                        .content(asJsonString(manufacturedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andReturn();

        // Assert that the MvcResult is not null
        Assertions.assertNotNull(mvcResult);

        // Assert that the response content is not null or empty
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(responseContent);
        Assertions.assertFalse(responseContent.isEmpty(), "Response content should not be empty.");

        // Optionally, you can add additional assertions on the response
        Assertions.assertTrue(responseContent.contains("\"id\":1"), "Response content should contain ID 1.");
    }


    @Test
    void testDeleteManufacturedProduct() throws Exception {
        long productId = 1L;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/manufactured-product/{id}", productId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token"))
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }
}
