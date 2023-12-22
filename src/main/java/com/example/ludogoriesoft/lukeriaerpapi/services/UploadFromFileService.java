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

    public String uploadFromFile(MultipartFile file) throws IOException {
        if (file != null) {
            try {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());
                Sheet sheet = workbook.getSheetAt(0);
                int lastRowNum = sheet.getLastRowNum();

                for (int i = 1; i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    int j = 1;

                    String nameValue = row.getCell(j++).getStringCellValue();
                    String productCode = row.getCell(0).getStringCellValue();


                    Cell availableQuantityCell = row.getCell(j++);
                    int availableQuantityValue;
                    if (availableQuantityCell.getCellType() == CellType.NUMERIC) {
                        availableQuantityValue = (int) availableQuantityCell.getNumericCellValue();
                    } else if (availableQuantityCell.getCellType() == CellType.STRING) {
                        availableQuantityValue = Integer.parseInt(availableQuantityCell.getStringCellValue());
                    } else if (availableQuantityCell.getCellType() == null) {
                        continue;
                    } else {
                        throw new IllegalStateException("Unsupported cell type for availableQuantityValue");
                    }

                    Cell cartonIdCell = row.getCell(j++);
                    Long cartonIdValue;
                    if (cartonIdCell.getCellType() == CellType.NUMERIC) {
                        cartonIdValue = (long) cartonIdCell.getNumericCellValue();
                    } else if (cartonIdCell.getCellType() == CellType.STRING) {
                        cartonIdValue = Long.valueOf(cartonIdCell.getStringCellValue());
                    } else {
                        throw new IllegalStateException("Unsupported cell type for cartonIdValue");
                    }

                    Cell plateIdCell = row.getCell(j++);
                    Long plateIdValue;
                    if (plateIdCell.getCellType() == CellType.NUMERIC) {
                        plateIdValue = (long) plateIdCell.getNumericCellValue();
                    } else if (plateIdCell.getCellType() == CellType.STRING) {
                        plateIdValue = Long.valueOf(plateIdCell.getStringCellValue());
                    } else {
                        throw new IllegalStateException("Unsupported cell type for plateIdValue");
                    }

                    Cell piecesCartonCell = row.getCell(j++);
                    int piecesCartonValue;
                    if (piecesCartonCell.getCellType() == CellType.NUMERIC) {
                        piecesCartonValue = (int) piecesCartonCell.getNumericCellValue();
                    } else if (piecesCartonCell.getCellType() == CellType.STRING) {
                        piecesCartonValue = Integer.parseInt(piecesCartonCell.getStringCellValue());
                    } else {
                        throw new IllegalStateException("Unsupported cell type for piecesCartonValue");
                    }

                    if (nameValue != null && !nameValue.isEmpty()) {
                        Package packageForCreate = new Package();
                        packageForCreate.setName(nameValue);
                        packageForCreate.setAvailableQuantity(availableQuantityValue);
                        packageForCreate.setCartonId(cartonService.getCartonByID(cartonIdValue));
                        packageForCreate.setPlateId(plateService.getPlateById(plateIdValue));
                        packageForCreate.setPiecesCarton(piecesCartonValue);
                        packageForCreate.setProductCode(productCode);
                        Package savedPackage = packageRepository.save(packageForCreate);

                        Product productForCreate = new Product();
                        productForCreate.setPackageId(savedPackage);
                        productForCreate.setProductCode(productCode);
                        productForCreate.setPrice(BigDecimal.valueOf(0.0));
                        productRepository.save(productForCreate);
                        if (179 == i) {
                            throw new IOException("File is Upload");
                        }
                    }
                }
                return "Успешно добавени всички пакети.";
            } catch (NotOfficeXmlFileException e) {
                throw new IOException("Файлът не е валиден .xlsx файл.", e);
            } catch (ChangeSetPersister.NotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "Файлът е null.";
        }
    }
}