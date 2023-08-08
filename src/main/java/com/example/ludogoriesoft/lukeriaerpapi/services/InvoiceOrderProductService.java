package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.InvoiceOrderProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.InvoiceOrderProduct;
import com.example.ludogoriesoft.lukeriaerpapi.repository.*;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceOrderProductService {
    private final InvoiceOrderProductRepository invoiceOrderProductRepository;
    private final OrderProductRepository orderProductRepository;
    private final PackageRepository packageRepository;
    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper modelMapper;

    public List<InvoiceOrderProductDTO> getAllInvoiceOrderProducts() {
        List<InvoiceOrderProduct> invoiceOrderProducts = invoiceOrderProductRepository.findByDeletedFalse();
        return invoiceOrderProducts.stream()
                .map(invoiceOrderProduct -> modelMapper.map(invoiceOrderProduct, InvoiceOrderProductDTO.class))
                .toList();
    }

    public InvoiceOrderProductDTO getInvoiceOrderProductById(Long id) throws ChangeSetPersister.NotFoundException {
        InvoiceOrderProduct invoiceOrderProduct = invoiceOrderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(invoiceOrderProduct, InvoiceOrderProductDTO.class);
    }

    public void deleteInvoiceOrderProduct(Long id) throws ChangeSetPersister.NotFoundException {
        InvoiceOrderProduct invoiceOrderProduct = invoiceOrderProductRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        invoiceOrderProduct.setDeleted(true);
        invoiceOrderProductRepository.save(invoiceOrderProduct);
    }

    void validateInvoiceOrderProduct(InvoiceOrderProductDTO invoiceOrderProductDTO) {
        if (invoiceOrderProductDTO.getOrderProductId() == null) {
            throw new ValidationException("OrderProduct ID cannot be null!");
        }

        boolean orderProductExists = orderProductRepository.existsById(invoiceOrderProductDTO.getOrderProductId());
        if (!orderProductExists) {
            throw new ValidationException("OrderProduct does not exist with ID: " + invoiceOrderProductDTO.getOrderProductId());
        }

        if (invoiceOrderProductDTO.getInvoiceId() == null) {
            throw new ValidationException("Invoice ID cannot be null!");
        }

        boolean invoiceExists = invoiceRepository.existsById(invoiceOrderProductDTO.getInvoiceId());
        if (!invoiceExists) {
            throw new ValidationException("Invoice does not exist with ID: " + invoiceOrderProductDTO.getInvoiceId());
        }
    }

    public InvoiceOrderProductDTO createInvoiceOrderProduct(InvoiceOrderProductDTO invoiceOrderProductDTO) {
        validateInvoiceOrderProduct(invoiceOrderProductDTO);
        InvoiceOrderProduct invoiceOrderProduct = invoiceOrderProductRepository.save(modelMapper.map(invoiceOrderProductDTO, InvoiceOrderProduct.class));
        return modelMapper.map(invoiceOrderProduct, InvoiceOrderProductDTO.class);
    }

    public InvoiceOrderProductDTO updateInvoiceOrderProduct(Long id, InvoiceOrderProductDTO invoiceOrderProductDTO) throws ChangeSetPersister.NotFoundException {
        validateInvoiceOrderProduct(invoiceOrderProductDTO);

        InvoiceOrderProduct existingInvoiceOrderProduct = invoiceOrderProductRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        InvoiceOrderProduct updatedInvoiceOrderProduct = modelMapper.map(invoiceOrderProductDTO, InvoiceOrderProduct.class);
        updatedInvoiceOrderProduct.setId(existingInvoiceOrderProduct.getId());
        invoiceOrderProductRepository.save(updatedInvoiceOrderProduct);
        return modelMapper.map(updatedInvoiceOrderProduct, InvoiceOrderProductDTO.class);
    }

}
