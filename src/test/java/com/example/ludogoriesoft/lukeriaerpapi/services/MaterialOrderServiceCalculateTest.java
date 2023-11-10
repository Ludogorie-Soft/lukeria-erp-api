package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.MaterialOrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MaterialOrderServiceCalculateTest {
    private MaterialOrderService materialOrderService;


    private CartonRepository cartonRepository;
    private Carton carton;

    @BeforeEach
    public void setUp() {
        // Създаваме мок на Carton
        carton = mock(Carton.class);
        // Инициализираме MaterialOrderService
        materialOrderService = new MaterialOrderService(null, null, cartonRepository, null, null, null, null);
    }

    @Test
    void testCreateCartonInsufficientMaterialOrder() {
        // Подготовка на данни: предполагаме, че имаме налични бройки на кашон (например 20)
        Package packageEntity = new Package();
        packageEntity.setId(1L);
        packageEntity.setAvailableQuantity(11);
        int availableQuantity = 20;
        int piecesCarton = 5;

        // Когато се извика packageEntity.getCartonId().getAvailableQuantity(), връщаме предварително зададената налична бройка
        when(carton.getAvailableQuantity()).thenReturn(availableQuantity);

        // Подготовка на Package с валиден кашон
        packageEntity.setCartonId(carton);
        packageEntity.setPiecesCarton(piecesCarton);

        // Подготовка на MaterialOrderDTO
        MaterialOrderDTO allNeedsMaterialOrder = new MaterialOrderDTO();
        allNeedsMaterialOrder.setOrderedQuantity(10);

        List<MaterialOrderDTO> allMaterialsForAllOrders = new ArrayList<>();

        // Извикваме метода, който тестваме
        materialOrderService.createCartonInsufficientMaterialOrder(allNeedsMaterialOrder, packageEntity, allMaterialsForAllOrders);

        // Проверяваме дали е създадена материална поръчка с правилните стойности
        assertEquals(0, allMaterialsForAllOrders.size()); // Очакваме 2 материални поръчки, тъй като броят на кашоните не е достатъчен (20 / 5) - (10 / 5) = 2
    }
}
