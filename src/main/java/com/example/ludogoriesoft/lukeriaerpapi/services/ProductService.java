package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;
    private final PackageRepository packageRepository;
    private final ModelMapper modelMapper;
    private final PlateRepository plateRepository;
    private final CartonRepository cartonRepository;

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByDeletedFalse();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }

    public ProductDTO getProductById(Long id) throws ChangeSetPersister.NotFoundException {
        Product product = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        validateProductDTO(productDTO);
        Product product = productRepository.save(modelMapper.map(productDTO, Product.class));
        return modelMapper.map(product, ProductDTO.class);
    }

    public List<ProductDTO> getProductsForSale() {
        List<Product> products = productRepository.getAvailableProductsForSale();
        Type listType = new TypeToken<List<ProductDTO>>() {}.getType();
        return modelMapper.map(products, listType);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ChangeSetPersister.NotFoundException {
        validateProductDTO(productDTO);

        Product existingProduct = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Product updatedProduct = modelMapper.map(productDTO, Product.class);
        updatedProduct.setId(existingProduct.getId());
        productRepository.save(updatedProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private void validateProductDTO(ProductDTO productDTO) {
        if (productDTO.getPrice().equals(BigDecimal.ZERO)) {
            throw new ValidationException("Price must be greater than zero");
        }
        if (productDTO.getAvailableQuantity() <= 0) {
            throw new ValidationException("Available quantity must be greater than zero");
        }
        if (productDTO.getPackageId() != null) {
            boolean existsPackage = packageRepository.existsById(productDTO.getPackageId());
            if (!existsPackage) {
                throw new ValidationException("Package does not exist with ID: " + productDTO.getPackageId());
            }
        } else {
            throw new ValidationException("Package ID cannot be null");
        }
    }

    public void deleteProduct(Long id) throws ChangeSetPersister.NotFoundException {
        Product product = productRepository.findByIdAndDeletedFalse(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        product.setDeleted(true);
        productRepository.save(product);
    }

    public ProductDTO produceProduct(Long productId, int producedQuantity) throws ChangeSetPersister.NotFoundException {
        Optional<Product> product = productRepository.findByIdAndDeletedFalse(productId);
        if (product.isPresent()) {
            product.get().setAvailableQuantity(product.get().getAvailableQuantity() + producedQuantity);
            productRepository.save(product.get());
            Optional<Package> aPackage = packageRepository.findByIdAndDeletedFalse(product.get().getPackageId().getId());
            if (aPackage.isPresent()) {
                aPackage.get().setAvailableQuantity(aPackage.get().getAvailableQuantity() - producedQuantity);
                packageRepository.save(aPackage.get());
            }
            Optional<Plate> plate = plateRepository.findByIdAndDeletedFalse(aPackage.get().getPlateId().getId());
            if (plate.isPresent()) {
                plate.get().setAvailableQuantity(plate.get().getAvailableQuantity() - producedQuantity);
                plateRepository.save(plate.get());
            }
            Optional<Carton> carton = cartonRepository.findByIdAndDeletedFalse(aPackage.get().getCartonId().getId());
            if (carton.isPresent()) {
                carton.get().setAvailableQuantity(carton.get().getAvailableQuantity() - producedQuantity / aPackage.get().getPiecesCarton());
                cartonRepository.save(carton.get());
            }
        }
        return modelMapper.map(product, ProductDTO.class);
    }
    public List<ProductDTO> getProductsForSaleWithoutLookingForQuantity() {
        List<Product> products = productRepository.getProductsForSale();
        Type listType = new TypeToken<List<ProductDTO>>() {}.getType();
        return modelMapper.map(products, listType);
    }
    public ProductDTO getProductByPackage(Long packageId){
        return modelMapper.map(productRepository.findByPackageId(packageRepository.findById(packageId).get()), ProductDTO.class);
    }
}
