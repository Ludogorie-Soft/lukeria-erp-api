package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.InvoiceOrderProductController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.InvoiceOrderProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = InvoiceOrderProductController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = InvoiceOrderProductController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class
                )
        }
)
public class InvoiceOrderProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InvoiceOrderProductController invoiceOrderProductController;
    @MockBean
    private InvoiceOrderProductService invoiceOrderProductService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInvoiceOrderProductById() throws Exception {
        Long productId = 1L;
        InvoiceOrderProductDTO mockProduct = new InvoiceOrderProductDTO();
        Mockito.when(invoiceOrderProductService.getInvoiceOrderProductById(productId)).thenReturn(mockProduct);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoiceOrderProduct/{id}", productId)
                        .header("Authorization", "your-auth-token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllInvoiceOrderProduct() throws Exception {
        List<InvoiceOrderProductDTO> mockProducts = Arrays.asList(new InvoiceOrderProductDTO(), new InvoiceOrderProductDTO());
        Mockito.when(invoiceOrderProductService.getAllInvoiceOrderProducts()).thenReturn(mockProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoiceOrderProduct")
                        .header("Authorization", "your-auth-token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createInvoiceOrderProduct() throws Exception {
        InvoiceOrderProductDTO requestDTO = new InvoiceOrderProductDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invoiceOrderProduct")
                        .header("Authorization", "your-auth-token")
                        .content(asJsonString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateInvoiceOrderProduct() throws Exception {
        Long productId = 1L;
        InvoiceOrderProductDTO requestDTO = new InvoiceOrderProductDTO();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/invoiceOrderProduct/{id}", productId)
                        .header("Authorization", "your-auth-token")
                        .content(asJsonString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteInvoiceOrderProductById() throws Exception {
        Long productId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/invoiceOrderProduct/{id}", productId)
                        .header("Authorization", "your-auth-token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
