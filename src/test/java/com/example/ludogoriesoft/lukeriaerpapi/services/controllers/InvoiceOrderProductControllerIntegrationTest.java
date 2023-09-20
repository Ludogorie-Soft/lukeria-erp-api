package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.InvoiceOrderProductController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.InvoiceOrderProductService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
 class InvoiceOrderProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceOrderProductService invoiceOrderProductService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testGetAllInvoiceOrderProduct() throws Exception {
//
//        InvoiceOrderProductDTO invoiceOrderProductDTO = new InvoiceOrderProductDTO();
//        invoiceOrderProductDTO.setId(1L);
//        InvoiceOrderProductDTO invoiceOrderProductDTO2 = new InvoiceOrderProductDTO();
//        invoiceOrderProductDTO2.setId(2L);
//        List<InvoiceOrderProductDTO> invoiceOrderProductDTOList = Arrays.asList(invoiceOrderProductDTO2, invoiceOrderProductDTO);
//
//        when(invoiceOrderProductService.getAllInvoiceOrderProducts()).thenReturn(invoiceOrderProductDTOList);
//
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoiceOrderProduct")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andReturn();
//
//        String response = mvcResult.getResponse().getContentAsString();
//        Assertions.assertNotNull(response);
//    }

//    @Test
//    void testGetInvoiceOrderProductById() throws Exception {
//
//        InvoiceOrderProductDTO invoiceOrderProductDTO = new InvoiceOrderProductDTO();
//        invoiceOrderProductDTO.setId(1L);
//
//
//        when(invoiceOrderProductService.getInvoiceOrderProductById(1L)).thenReturn(invoiceOrderProductDTO);
//
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoiceOrderProduct/{id}", 1)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andReturn();
//
//        String response = mvcResult.getResponse().getContentAsString();
//        Assertions.assertNotNull(response);
//    }

//    @Test
//    void testUpdateMaterialOrder() throws Exception {
//        InvoiceOrderProductDTO invoiceOrderProductDTO = new InvoiceOrderProductDTO();
//        invoiceOrderProductDTO.setId(1L);
//
//        when(invoiceOrderProductService.updateInvoiceOrderProduct(anyLong(), any(InvoiceOrderProductDTO.class))).thenReturn(invoiceOrderProductDTO);
//
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/invoiceOrderProduct/{id}", 1)
//                        .content("{\"id\": 1, \"invoiceId\": \"3\"}")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andReturn();
//
//        String response = mvcResult.getResponse().getContentAsString();
//        Assertions.assertNotNull(response);
//    }
//
//
//    @Test
//    void testDeleteInvoiceOrderProductById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/invoiceOrderProduct/{id}", 1))
//                .andExpect(status().isOk())
//                .andExpect(content().string("InvoiceOrderProduct with id: 1 has been deleted successfully!"));
//    }

}
