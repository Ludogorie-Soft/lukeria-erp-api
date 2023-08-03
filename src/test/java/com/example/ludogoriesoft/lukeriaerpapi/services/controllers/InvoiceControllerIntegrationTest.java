package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.InvoiceController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.InvoiceService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = InvoiceController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = InvoiceController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class
                )
        }
)
class InvoiceControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInvoices() throws Exception {
        InvoiceDTO invoiceDTO1 = new InvoiceDTO();
        invoiceDTO1.setId(1L);
        invoiceDTO1.setInvoiceNumber(1L);
        InvoiceDTO invoiceDTO2 = new InvoiceDTO();
        invoiceDTO2.setId(2L);
        invoiceDTO2.setInvoiceNumber(1L);
        List<InvoiceDTO> invoiceDTOList = Arrays.asList(invoiceDTO1, invoiceDTO2);

        when(invoiceService.getAllInvoices()).thenReturn(invoiceDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoice")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].invoiceNumber").value(1L))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].invoiceNumber").value(1L))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testGetInvoiceById() throws Exception {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber(1L);

        when(invoiceService.getInvoiceById(anyLong())).thenReturn(invoiceDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoice/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.invoiceNumber").value(1L))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreateInvoice() throws Exception {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber(1L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(100));
        when(invoiceService.createInvoice(any(InvoiceDTO.class))).thenReturn(invoiceDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"invoiceNumber\": \"1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.invoiceNumber").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdateInvoice() throws Exception {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber(1L);
        invoiceDTO.setTotalPrice(BigDecimal.valueOf(100));
        when(invoiceService.updateInvoice(anyLong(), any(InvoiceDTO.class))).thenReturn(invoiceDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/invoice/{id}", 1)
                        .content("{\"id\": 1, \"invoiceNumber\": \"1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.invoiceNumber").value(1))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeleteInvoiceById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/invoice/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Invoice with id: 1 has been deleted successfully!"));
    }

    @Test
    void testGetAllInvoicesWhenNoInvoiceExist() throws Exception {
        when(invoiceService.getAllInvoices()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoice")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    void testGetInvoiceByIdWhenInvoiceDoesNotExist() throws Exception {
        long invoiceId = 1L;
        when(invoiceService.getInvoiceById(invoiceId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoice/{id}", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testShouldNotCreateInvoiceWithBlankInvoiceName() throws Exception {
        String blankInvoiceName = "";

        doThrow(new ValidationException())
                .when(invoiceService).createInvoice(any(InvoiceDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invoice")
                        .content("{\"id\": 1, \"invoiceNumber\": \"" + blankInvoiceName + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testGetInvoiceWithInvalidId() throws Exception {
        Long invalidId = 100000L;

        when(invoiceService.getInvoiceById(invalidId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoice/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testUpdateInvoiceWithInvalidData() throws Exception {
        String invalidData = "";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/invoice/{id}", 1)
                        .content("{\"id\": 1, \"invoiceNumber\": " + invalidData + "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateInvoiceWithInvalidIdShouldReturnNotFound() throws Exception {
        Long id = 1L;

        InvoiceDTO updatedInvoice = new InvoiceDTO();

        when(invoiceService.updateInvoice(eq(id), any(InvoiceDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/invoice/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedInvoice)))
                .andExpect(status().isNotFound());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteInvoiceByIdWhenInvoiceDoesNotExist() throws Exception {
        long invoiceId = 1L;
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(invoiceService).deleteInvoice(invoiceId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/invoice/{id}", invoiceId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }
}

