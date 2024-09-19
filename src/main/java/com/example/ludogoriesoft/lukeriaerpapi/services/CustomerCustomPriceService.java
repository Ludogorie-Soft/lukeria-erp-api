package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CustomerCustomPriceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Client;
import com.example.ludogoriesoft.lukeriaerpapi.models.CustomerCustomPrice;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CustomerCustomPriceRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerCustomPriceService {
    private final CustomerCustomPriceRepository customerCustomPriceRepository;
    private final ModelMapper modelMapper;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    private void validation(CustomerCustomPriceDTO customerCustomPriceDTO) {
        if (customerCustomPriceDTO.getPrice().equals(BigDecimal.ZERO)) {
            throw new ValidationException("Price must be greater than zero");
        }

        // Check for null Client ID first
        if (customerCustomPriceDTO.getClientId() == null) {
            throw new ValidationException("Client ID cannot be null");
        }

        // Check if the client exists
        if (!clientRepository.existsById(customerCustomPriceDTO.getClientId())) {
            throw new ValidationException("Client does not exist with ID: " + customerCustomPriceDTO.getClientId());
        }

        // Check for null Product ID
        if (customerCustomPriceDTO.getProductId() == null) {
            throw new ValidationException("Product ID cannot be null");
        }

        // Check if the product exists
        if (!productRepository.existsById(customerCustomPriceDTO.getProductId())) {
            throw new ValidationException("Product does not exist with ID: " + customerCustomPriceDTO.getProductId());
        }
    }

    public CustomerCustomPriceDTO create(CustomerCustomPriceDTO customerCustomPriceDTO) throws ChangeSetPersister.NotFoundException {
        validation(customerCustomPriceDTO);
        CustomerCustomPrice customerCustomPrice = customerCustomPriceRepository.save(modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class));
        return modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class);
    }

    public CustomerCustomPriceDTO update(CustomerCustomPriceDTO customerCustomPriceDTO) throws ChangeSetPersister.NotFoundException {
        Client client = clientRepository.findByIdAndDeletedFalse(customerCustomPriceDTO.getClientId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Product product = productRepository.findByIdAndDeletedFalse(customerCustomPriceDTO.getProductId()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        CustomerCustomPrice existingCustomPrice = findByClientIdAndProductId(client,product);
        validation(customerCustomPriceDTO);
        CustomerCustomPrice updatedCustomPrice = modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class);
        updatedCustomPrice.setId(existingCustomPrice.getId());
        customerCustomPriceRepository.save(updatedCustomPrice);
        return modelMapper.map(updatedCustomPrice, CustomerCustomPriceDTO.class);
    }

    public List<CustomerCustomPriceDTO> getAllCustomPrices() {
        List<CustomerCustomPrice> allCustomPrices = customerCustomPriceRepository.findByDeletedFalse();
        return allCustomPrices.stream().map(customPrice -> modelMapper.map(customPrice, CustomerCustomPriceDTO.class)).toList();
    }

    public CustomerCustomPriceDTO delete(Long clientId, Long productId) throws ChangeSetPersister.NotFoundException {
        Client client = clientRepository.findByIdAndDeletedFalse(clientId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Product product = productRepository.findByIdAndDeletedFalse(productId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        CustomerCustomPrice existingCustomPrice = findByClientIdAndProductId(client,product);
        existingCustomPrice.setDeleted(true);
        customerCustomPriceRepository.save(existingCustomPrice);
        return modelMapper.map(existingCustomPrice, CustomerCustomPriceDTO.class);
    }

    private List<CustomerCustomPriceDTO> findByClientId(Client clientId) {
        List<CustomerCustomPrice> foundByClientId = customerCustomPriceRepository.findByClientIdAndDeletedFalse(clientId);
        return foundByClientId.stream().map(customPrice -> modelMapper.map(customPrice, CustomerCustomPriceDTO.class)).toList();
    }

    public List<CustomerCustomPriceDTO> allProductWithCustomPriceForClient(Long clientId) throws ChangeSetPersister.NotFoundException {
        Client client = clientRepository.findByIdAndDeletedFalse(clientId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return findByClientId(client);
    }

    private CustomerCustomPrice findByClientIdAndProductId(Client clientId, Product productId) throws ChangeSetPersister.NotFoundException {
        CustomerCustomPrice customerCustomPrice = customerCustomPriceRepository.findByClientIdAndProductIdAndDeletedFalse(clientId, productId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return customerCustomPrice;
    }

}
