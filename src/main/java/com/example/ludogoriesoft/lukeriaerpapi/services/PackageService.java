package com.example.ludogoriesoft.lukeriaerpapi.services;


import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.exeptions.ApiRequestException;
import com.example.ludogoriesoft.lukeriaerpapi.mappers.PackageMapper;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PackageService {
    private final PackageRepository packageRepository;
    private final PackageMapper mapper;

    public PackageDTO toDTO(Package packageEntity) {
        return mapper.toDto(packageEntity);
    }

    public Package toEntity(PackageDTO packageDTO) {
        return mapper.toEntity(packageDTO);
    }


    public List<PackageDTO> getAllPackages() {
        List<Package> packages = packageRepository.findAll();
        return packages
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PackageDTO getPackageById(Long id) {
        Optional<Package> optionalPackage = packageRepository.findById(id);
        if (optionalPackage.isEmpty()) {
            throw new ApiRequestException("Package with id: " + id + " Not Found");
        }
        return toDTO(optionalPackage.get());
    }

    public PackageDTO createPackage(PackageDTO packageDTO) {
        if (StringUtils.isBlank(packageDTO.getName())) {
            throw new ApiRequestException("Package is blank");
        }
           Package packageEntity=packageRepository.save(toEntity(packageDTO));
        return toDTO(packageEntity);
    }


    public PackageDTO updatePackage(Long id, PackageDTO packageDTO) {
        Optional<Package> optionalPackage = packageRepository.findById(id);
        if (optionalPackage.isEmpty()) {
            throw new ApiRequestException("Package with id: " + id + " Not Found");
        }

        Package existingPackage = optionalPackage.get();

        if (packageDTO == null || packageDTO.getName() == null || packageDTO.getPiecesCarton() == 0
                || packageDTO.getAvailableQuantity() == 0) {
            throw new ApiRequestException("Invalid Package data");
        }
        if (packageDTO.getName() != null) {
            existingPackage.setName(packageDTO.getName());
        }
        if (packageDTO.getPiecesCarton() != 0) {
            existingPackage.setPiecesCarton(packageDTO.getPiecesCarton());
        }
        if (packageDTO.getAvailableQuantity() != 0) {
            existingPackage.setAvailableQuantity(packageDTO.getAvailableQuantity());
        }
        if (packageDTO.getPhoto() != null) {
            existingPackage.setPhoto(packageDTO.getPhoto());
        }
        if (packageDTO.getPrice() != 0) {
            existingPackage.setPrice(packageDTO.getPrice());
        }

        Package updatedPackage = packageRepository.save(existingPackage);
        updatedPackage.setId(id);
        return toDTO(updatedPackage);
    }


    public void deletePackage(Long id) {
        Optional<Package> packageOptional = packageRepository.findById(id);
        if (packageOptional.isEmpty()) {
            throw new ApiRequestException("Package not found for id " + id);
        }
        packageRepository.delete(packageOptional.get());
    }


}
