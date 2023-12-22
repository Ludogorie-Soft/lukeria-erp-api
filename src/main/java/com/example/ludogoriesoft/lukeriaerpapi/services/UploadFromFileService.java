package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UploadFromFileService {
    private final PackageRepository packageRepository;
    private final ProductRepository productRepository;
    private final PlateService plateService;
    private final CartonService cartonService;

  public ResponseEntity<String> packageUploadStatus(MultipartFile file) throws IOException {
    if (packageRepository.findAll().isEmpty()) {
      return uploadFromFile(file);
    }return ResponseEntity.ok("File is already uploaded!");
  }

        private ResponseEntity<String> uploadFromFile(MultipartFile file) throws IOException {
        if (file != null) {
            try {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                int lastRowNum = sheet.getLastRowNum();

                for (int i = 1; i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    int j = 1;

                    String productCode = row.getCell(0).getStringCellValue();
                    String nameValue = row.getCell(j++).getStringCellValue();


                    Cell availableQuantityCell = row.getCell(j++);
                    int availableQuantityValue;
                    availableQuantityValue = (int) availableQuantityCell.getNumericCellValue();


                    Cell cartonIdCell = row.getCell(j++);
                    long cartonIdValue;
                    cartonIdValue = (long) cartonIdCell.getNumericCellValue();

                    Cell plateIdCell = row.getCell(j++);
                    long plateIdValue;
                    plateIdValue = (long) plateIdCell.getNumericCellValue();


                    Cell piecesCartonCell = row.getCell(j++);
                    int piecesCartonValue;
                    piecesCartonValue = (int) piecesCartonCell.getNumericCellValue();

                    Cell pricePackageCell = row.getCell(j++);
                    BigDecimal pricePackageValue;
                    pricePackageValue= BigDecimal.valueOf(pricePackageCell.getNumericCellValue());

                    String englishNameValue = row.getCell(++j).getStringCellValue();

                    if (nameValue != null && !nameValue.isEmpty()) {
                        Package packageForCreate = new Package();
                        packageForCreate.setName(nameValue);
                        packageForCreate.setAvailableQuantity(availableQuantityValue);
                        packageForCreate.setCartonId(cartonService.getCartonByID(cartonIdValue));
                        packageForCreate.setPlateId(plateService.getPlateById(plateIdValue));
                        packageForCreate.setPiecesCarton(piecesCartonValue);
                        packageForCreate.setProductCode(productCode);
                        packageForCreate.setPrice(pricePackageValue);
                        packageForCreate.setEnglishName(englishNameValue);
                        Package savedPackage = packageRepository.save(packageForCreate);

                        Product productForCreate = new Product();
                        productForCreate.setPackageId(savedPackage);
                        productForCreate.setProductCode(productCode);
                        productForCreate.setPrice(BigDecimal.valueOf(0.0));
                        productRepository.save(productForCreate);
                    }
                }
                return ResponseEntity.ok("Successfully added all packages.");
            } catch (NotOfficeXmlFileException e) {
                throw new IOException("The file is not a valid .xlsx file.", e);
            } catch (ChangeSetPersister.NotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("File is null.");
        }
    }
}