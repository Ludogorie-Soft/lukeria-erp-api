package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.PlateController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.PlateService;
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
@WebMvcTest(value = PlateController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = PlateController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class
                )
        }
)
class PlateControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlateService plateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPlates() throws Exception {
        PlateDTO plateDTO1 = new PlateDTO();
        plateDTO1.setId(1L);
        plateDTO1.setName("Plate 1");
        PlateDTO plateDTO2 = new PlateDTO();
        plateDTO2.setId(2L);
        plateDTO2.setName("Plate 2");
        List<PlateDTO> plateDTOList = Arrays.asList(plateDTO1, plateDTO2);

        when(plateService.getAllPlates()).thenReturn(plateDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Plate 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Plate 2"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testGetPlateById() throws Exception {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setName("Plate 1");

        when(plateService.getPlateById(anyLong())).thenReturn(plateDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plate/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Plate 1"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreatePlate() throws Exception {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setName("New Plate");

        when(plateService.createPlate(any(PlateDTO.class))).thenReturn(plateDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"New Plate\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Plate"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdatePlate() throws Exception {
        PlateDTO plateDTO = new PlateDTO();
        plateDTO.setId(1L);
        plateDTO.setName("Updated Plate");

        when(plateService.updatePlate(anyLong(), any(PlateDTO.class))).thenReturn(plateDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/plate/{id}", 1)
                        .content("{\"id\": 1, \"name\": \"Updated Plate\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Plate"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeletePlateById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/plate/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Plate with id: 1 has been deleted successfully!"));
    }

    @Test
    void testGetAllPlatesWhenNoPlateExist() throws Exception {
        when(plateService.getAllPlates()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    void testGetPlateByIdWhenPlateDoesNotExist() throws Exception {
        long plateId = 1L;
        when(plateService.getPlateById(plateId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plate/{id}", plateId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testShouldNotCreatePlateWithBlankPlateName() throws Exception {
        String blankPlateName = "";

        doThrow(new ValidationException())
                .when(plateService).createPlate(any(PlateDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plate")
                        .content("{\"id\": 1, \"name\": \"" + blankPlateName + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testGetPlateWithInvalidId() throws Exception {
        Long invalidId = 100000L;

        when(plateService.getPlateById(invalidId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plate/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testUpdatePlateWithInvalidData() throws Exception {
        String invalidData = "";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/plate/{id}", 1)
                        .content("{\"id\": 1, \"name\": " + invalidData + "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePlateWithInvalidIdShouldReturnNotFound() throws Exception {
        Long id = 1L;

        PlateDTO updatedPlate = new PlateDTO();

        when(plateService.updatePlate(eq(id), any(PlateDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/plate/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedPlate)))
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
    void testDeletePlateByIdWhenPlateDoesNotExist() throws Exception {
        long plateId = 1L;
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(plateService).deletePlate(plateId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/plate/{id}", plateId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }
}
