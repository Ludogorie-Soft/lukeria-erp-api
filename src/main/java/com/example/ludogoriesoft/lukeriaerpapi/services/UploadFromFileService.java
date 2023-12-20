package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Product;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UploadFromFileService {
  private final PackageRepository packageRepository;
  private final ProductRepository productRepository;

  public String uploadFromFile(MultipartFile file) throws IOException {
    if (file != null) {
      try {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();

        for (int i = 11; i <= lastRowNum; i++) {
          Row row = sheet.getRow(i);

          // Проверка дали клетката с индекс 2 на реда не е null
          Cell cell = row.getCell(2);
          if (cell != null
              && cell.getStringCellValue() != null
              && !cell.getStringCellValue().isEmpty()) {
            String valueFromColumn = cell.getStringCellValue();

            Package packageForCreate = new Package();
            packageForCreate.setName(valueFromColumn);
            Package savedPackage = packageRepository.save(packageForCreate);

            Product productForCreate = new Product();
            productForCreate.setPackageId(savedPackage);
            productForCreate.setPrice(BigDecimal.valueOf(0.01));
            productRepository.save(productForCreate);
          }
        }

        return "Успешно добавени всички пакети.";
      } catch (NotOfficeXmlFileException e) {
        throw new IOException("Файлът не е валиден .xlsx файл.", e);
      }
    } else {
      return "Файлът е null.";
    }
  }
}
