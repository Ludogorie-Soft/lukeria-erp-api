package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.CartonDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.PlateDTO;
import com.example.ludogoriesoft.lukeriaerpapi.dtos.ProductDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.*;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class PackageService {
  private final PackageRepository packageRepository;
  private final CartonRepository cartonRepository;
  private final PlateRepository plateRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final ImageService imageService;
  private final ProductService productService;
  private final EmailService emailService;
  private final EmailContentBuilder emailContentBuilder;

  public List<PackageDTO> getAllPackages() {
    List<Package> packages = packageRepository.findByDeletedFalse();
    return packages.stream().map(package1 -> modelMapper.map(package1, PackageDTO.class)).toList();
  }

  public PackageDTO getPackageById(Long id) throws ChangeSetPersister.NotFoundException {
    Package package1 =
        packageRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(ChangeSetPersister.NotFoundException::new);
    return modelMapper.map(package1, PackageDTO.class);
  }


  public Boolean sendProductStockReportById(Long packageId) throws ChangeSetPersister.NotFoundException {
    Package productPackage = findPackageById(packageId);
    PackageDTO packageDTO = getPackageById(packageId);

    PlateDTO plateDTO = productPackage.getPlateId() != null ? modelMapper.map(productPackage.getPlateId(), PlateDTO.class) : null;
    CartonDTO cartonDTO = productPackage.getCartonId() != null ? modelMapper.map(productPackage.getCartonId(), CartonDTO.class) : null;
    ProductDTO productDTO = productService.getProductById(productPackage.getId());

    String emailBody = emailContentBuilder.generateProductStockReportById(productDTO, packageDTO, plateDTO, cartonDTO);
    emailService.sendHtmlEmailWithProductReport(userRepository.findEmailsByRoleNotCustomer(), "Наличност за Продукт с Код: " + packageDTO.getProductCode(), emailBody);
    return true;
  }

  private Package findPackageById(Long packageId) throws ChangeSetPersister.NotFoundException {
    return packageRepository.findByIdAndDeletedFalse(packageId)
            .orElseThrow(ChangeSetPersister.NotFoundException::new);
  }
  public PackageDTO createPackage(PackageDTO packageDTO) {
    validatePackageDTO(packageDTO);
    Package packageEntity = packageRepository.save(modelMapper.map(packageDTO, Package.class));
    return modelMapper.map(packageEntity, PackageDTO.class);
  }

  public PackageDTO updatePackage(Long id, PackageDTO packageDTO)
      throws ChangeSetPersister.NotFoundException {
    validatePackageDTO(packageDTO);

    Package existingPackage =
        packageRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(ChangeSetPersister.NotFoundException::new);

    if (existingPackage.getPhoto() != null && packageDTO.getPhoto() != null) {
      imageService.deleteImageFromSpace(existingPackage.getPhoto());
    } else {
      packageDTO.setPhoto(existingPackage.getPhoto());
    }

    Package updatedPackage = modelMapper.map(packageDTO, Package.class);
    updatedPackage.setId(existingPackage.getId());
    packageRepository.save(updatedPackage);
    return modelMapper.map(updatedPackage, PackageDTO.class);
  }

  private void validatePackageDTO(PackageDTO packageDTO) {
    if (StringUtils.isBlank(packageDTO.getName())) {
      throw new ValidationException("Name is required");
    }
    if (packageDTO.getPiecesCarton() <= 0) {
      throw new ValidationException("Pieces of carton must be greater than zero");
    }
    if (packageDTO.getAvailableQuantity() <= 0) {
      throw new ValidationException("Available quantity must be greater than zero");
    }
    if (packageDTO.getPrice().doubleValue() <= 0) {
      throw new ValidationException("Price must be greater than zero");
    }
    if (!packageDTO.getEnglishName().matches("^[a-zA-Z0-9\s!@#$%^&*()-_=+'\"]*$")) {
      throw new ValidationException("English name can contain only letters English");
    }
    if (packageDTO.getCartonId() != null) {
      boolean cartonExists = cartonRepository.existsById(packageDTO.getCartonId());
      if (!cartonExists) {
        throw new ValidationException("Carton does not exist with ID: " + packageDTO.getCartonId());
      }
    } else {
      throw new ValidationException("Carton ID cannot be null");
    }
    if (packageDTO.getPlateId() != null) {
      boolean plateExists = plateRepository.existsById(packageDTO.getPlateId());
      if (!plateExists) {
        throw new ValidationException("Plate does not exist with ID: " + packageDTO.getPlateId());
      }
    } else {
      throw new ValidationException("Plate ID cannot be null");
    }
  }

  public void deletePackage(Long id) throws ChangeSetPersister.NotFoundException {
    Package package1 =
        packageRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(ChangeSetPersister.NotFoundException::new);
    package1.setDeleted(true);
    packageRepository.save(package1);
  }
}
