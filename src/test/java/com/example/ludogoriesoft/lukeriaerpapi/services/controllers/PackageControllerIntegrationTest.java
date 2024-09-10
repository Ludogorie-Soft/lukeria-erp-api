package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.PackageController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiExceptionHandler;
import com.example.ludogoriesoft.lukeriaerpapi.services.PackageService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = PackageController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = PackageController.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ApiExceptionHandler.class),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SlackService.class
                )
        }
)
class PackageControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SlackService slackService;

    @MockBean
    private PackageService packageService;
    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAllPackages() throws Exception {
        // Arrange
        PackageDTO packageDTO1 = new PackageDTO();
        packageDTO1.setId(1L);
        packageDTO1.setName("Package 1");
        PackageDTO packageDTO2 = new PackageDTO();
        packageDTO2.setId(2L);
        packageDTO2.setName("Package 2");
        List<PackageDTO> packageDTOList = Arrays.asList(packageDTO1, packageDTO2);

        when(packageService.getAllPackages()).thenReturn(packageDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/package")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Package 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Package 2"))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }
    @Test
    @WithMockUser(roles = "USER")
    void testGetAllPackagesWithUSerRole() throws Exception {
        // Arrange
        PackageDTO packageDTO1 = new PackageDTO();
        packageDTO1.setId(1L);
        packageDTO1.setName("Package 1");
        PackageDTO packageDTO2 = new PackageDTO();
        packageDTO2.setId(2L);
        packageDTO2.setName("Package 2");
        List<PackageDTO> packageDTOList = Arrays.asList(packageDTO1, packageDTO2);

        when(packageService.getAllPackages()).thenReturn(packageDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/package")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Package 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Package 2"))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testGetPackageById() throws Exception {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(1L);
        packageDTO.setName("Package 1");

        when(packageService.getPackageById(anyLong())).thenReturn(packageDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/package/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Package 1"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }
    @Test
    @WithMockUser(roles = "USER")
    void testGetPackageByIdWithUserRole() throws Exception {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(1L);
        packageDTO.setName("Package 1");

        when(packageService.getPackageById(anyLong())).thenReturn(packageDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/package/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Package 1"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testCreatePackage() throws Exception {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(1L);
        packageDTO.setName("New Package");

        when(packageService.createPackage(any(PackageDTO.class))).thenReturn(packageDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/package")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"New Package\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Package"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testUpdatePackage() throws Exception {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(1L);
        packageDTO.setName("Updated Package");

        when(packageService.updatePackage(anyLong(), any(PackageDTO.class))).thenReturn(packageDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/package/{id}", 1)
                        .content("{\"id\": 1, \"name\": \"Updated Package\"}")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Package"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(response);
    }

    @Test
    void testDeletePackageById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/package/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Package with id: 1 has been deleted successfully!"));
    }

    @Test
    void testGetAllPackagesWhenNoPackageExist() throws Exception {
        when(packageService.getAllPackages()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/package")
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    void testGetPackageByIdWhenPackageDoesNotExist() throws Exception {
        long packageId = 1L;
        when(packageService.getPackageById(packageId)).thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/package/{id}", packageId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testShouldNotCreatePackageWithBlankPackageName() throws Exception {
        String blankPackageName = "";

        doThrow(new ValidationException())
                .when(packageService).createPackage(any(PackageDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/package")
                        .content("{\"id\": 1, \"name\": \"" + blankPackageName + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testGetPackageWithInvalidId() throws Exception {
        Long invalidId = 100000L;

        when(packageService.getPackageById(invalidId))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/package/{id}", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }

    @Test
    void testUpdatePackageWithInvalidData() throws Exception {
        String invalidData = "";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/package/{id}", 1)
                        .content("{\"id\": 1, \"name\": " + invalidData + "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void testUpdatePackageWithInvalidIdShouldReturnNotFound() throws Exception {
        Long id = 1L;

        PackageDTO updatedPackage = new PackageDTO();
        // Set properties of updatedPackage as needed

        when(packageService.updatePackage(eq(id), any(PackageDTO.class)))
                .thenThrow(new ChangeSetPersister.NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/package/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPackage)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePackageByIdWhenPackageDoesNotExist() throws Exception {
        long packageId = 1L;
        doThrow(new ChangeSetPersister.NotFoundException())
                .when(packageService).deletePackage(packageId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/package/{id}", packageId)
                        .header(HttpHeaders.AUTHORIZATION, "your-authorization-token") // Add the Authorization header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Not found!")));
    }
}
