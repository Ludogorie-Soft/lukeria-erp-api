package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CustomerCustomPriceDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.OrderDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.CustomerCustomPrice;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ClientRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CustomerCustomPriceRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
        if (customerCustomPriceDTO.getClientId() != null) {
            boolean existsClient = clientRepository.existsById(customerCustomPriceDTO.getClientId());
            if (!existsClient) {
                throw new ValidationException("Client does not exist with ID: " + customerCustomPriceDTO.getClientId());
            }
        } else {
            throw new ValidationException("Client ID cannot be null");
        }
        if (customerCustomPriceDTO.getProductId() != null) {
            boolean existsProduct = productRepository.existsById(customerCustomPriceDTO.getProductId());
            if (!existsProduct) {
                throw new ValidationException("Product does not exist with ID: " + customerCustomPriceDTO.getProductId());
            }
        } else {
            throw new ValidationException("Product ID cannot be null");
        }
    }

    public CustomerCustomPriceDTO create(CustomerCustomPriceDTO customerCustomPriceDTO) throws ChangeSetPersister.NotFoundException {
        validation(customerCustomPriceDTO);
        CustomerCustomPrice customerCustomPrice = customerCustomPriceRepository.save(modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class));
        return modelMapper.map(customerCustomPrice, CustomerCustomPriceDTO.class);
    }

    public CustomerCustomPriceDTO update(Long id, CustomerCustomPriceDTO customerCustomPriceDTO) throws ChangeSetPersister.NotFoundException {
        validation(customerCustomPriceDTO);
        CustomerCustomPrice existingCustomPrice = customerCustomPriceRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        CustomerCustomPrice updatedCustomPrice = modelMapper.map(customerCustomPriceDTO, CustomerCustomPrice.class);
        updatedCustomPrice.setId(existingCustomPrice.getId());
        customerCustomPriceRepository.save(updatedCustomPrice);
        return modelMapper.map(updatedCustomPrice, CustomerCustomPriceDTO.class);
    }

    public List<CustomerCustomPriceDTO> getAllCustomPrices(){
        List<CustomerCustomPrice> allCustomPrices = customerCustomPriceRepository.findByDeletedFalse();
        return allCustomPrices.stream().map(customPrice -> modelMapper.map(customPrice, CustomerCustomPriceDTO.class)).toList();
    }
    public CustomerCustomPriceDTO delete(Long id) throws ChangeSetPersister.NotFoundException {
        CustomerCustomPrice existingCustomPrice = customerCustomPriceRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        existingCustomPrice.setDeleted(true);
        customerCustomPriceRepository.save(existingCustomPrice);
        return modelMapper.map(existingCustomPrice, CustomerCustomPriceDTO.class);
    }

}
