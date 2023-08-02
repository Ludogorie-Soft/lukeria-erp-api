package com.example.ludogoriesoft.lukeriaerpapi.services.controllers;

import com.example.ludogoriesoft.lukeriaerpapi.controllers.MaterialOrderController;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.enums.MaterialType;
import com.example.ludogoriesoft.lukeriaerpapi.models.MaterialOrder;
import com.example.ludogoriesoft.lukeriaerpapi.repository.MaterialOrderRepository;
import com.example.ludogoriesoft.lukeriaerpapi.services.MaterialOrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(MaterialOrderController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})

public class MaterialOrderControllerTest {

    @Mock
    private MaterialOrderRepository materialOrderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MaterialOrderService materialOrderService;

    private MaterialOrderController materialOrderController;
    @Mock
    private MockMvc mockMvc;


    @Before
    public void setup() {
        List<MaterialOrder> mockMaterialOrderList = new ArrayList<>();
        MaterialOrder materialOrder1 = new MaterialOrder(1L, 10, 5, 101L, MaterialType.PACKAGE, BigDecimal.valueOf(50.00), LocalDate.now(), false);
        MaterialOrder materialOrder2 = new MaterialOrder(2L, 20, 15, 102L, MaterialType.PLATE, BigDecimal.valueOf(30.00), LocalDate.now(), false);
        MaterialOrder materialOrder3 = new MaterialOrder(3L, 5, 3, 103L, MaterialType.CARTON, BigDecimal.valueOf(20.00), LocalDate.now(), false);
        mockMaterialOrderList.add(materialOrder1);
        mockMaterialOrderList.add(materialOrder2);
        mockMaterialOrderList.add(materialOrder3);

        when(materialOrderRepository.findByDeletedFalse()).thenReturn(mockMaterialOrderList);
        materialOrderController = new MaterialOrderController(materialOrderService);

    }

    @Test
    public void testGetAllMaterialOrders() {
        List<MaterialOrderDTO> result = materialOrderController.getAllMaterialOrders().getBody();

        assert result != null;
        assertEquals(3, result.size());
    }

}