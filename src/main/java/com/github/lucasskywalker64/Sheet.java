package com.github.lucasskywalker64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The Sheet class manages the creation and manipulation of Excel sheets for storing data.
 */
public class Sheet {

  private Workbook workbook;
  private String sheetName;
  private String fileLocation;

  /**
   * Retrieves the file location of the Excel sheet.
   *
   * @return The file location of the Excel sheet.
   */
  public String getFileLocation() {
    return fileLocation;
  }

  /**
   * Writes data from the given KostalData object to the Excel sheet.
   *
   * @param data The KostalData object containing the data to be written.
   * @throws IOException If an I/O error occurs.
   */
  public void writeToFile(KostalData data) throws IOException {
    CellStyle dateStyle = workbook.createCellStyle();
    dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat()
        .getFormat("dd/mm/yy hh:mm:ss"));

    CellStyle numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.createDataFormat().getFormat(BuiltinFormats
        .getBuiltinFormat(3)));

    CellStyle percentStyle = workbook.createCellStyle();
    percentStyle.setDataFormat(workbook.createDataFormat().getFormat(BuiltinFormats
        .getBuiltinFormat(9)));

    org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(sheetName);
    Map<String, ArrayList<Object>> listMap = data.getDataAndClear();
    for (int i = 1; i <= listMap.get("timestamp").size(); i++) {
      Row row = sheet.createRow(i);
      row.createCell(0).setCellValue((Timestamp) listMap.get("timestamp").get(i - 1));
      row.getCell(0).setCellStyle(dateStyle);
      row.createCell(1).setCellValue((int) listMap.get("dcInput1").get(i - 1));
      row.getCell(1).setCellStyle(numberStyle);
      row.createCell(2).setCellValue((int) listMap.get("dcInput2").get(i - 1));
      row.getCell(2).setCellStyle(numberStyle);
      row.createCell(3).setCellValue((int) listMap.get("batteryCharge").get(i - 1));
      row.getCell(3).setCellStyle(numberStyle);
      row.createCell(4).setCellValue((int) listMap.get("consFromPV").get(i - 1));
      row.getCell(4).setCellStyle(numberStyle);
      row.createCell(5).setCellValue((int) listMap.get("consFromBattery").get(i - 1));
      row.getCell(5).setCellStyle(numberStyle);
      row.createCell(6).setCellValue((int) listMap.get("gridPurchase").get(i - 1));
      row.getCell(6).setCellStyle(numberStyle);
      row.createCell(7).setCellValue((int) listMap.get("gridFeedIn").get(i - 1));
      row.getCell(7).setCellStyle(numberStyle);
    }
    FileOutputStream stream = new FileOutputStream(fileLocation);
    workbook.write(stream);
    workbook.close();
  }

  /**
   * Creates a new Excel sheet with predefined headers and column widths.
   */
  public void createSheet() {
    File currDir = new File(".");
    String path = currDir.getAbsolutePath();
    fileLocation = path.substring(0, path.length() - 1)
            + new SimpleDateFormat("dd-MM-yy'.xlsx'").format(new Date());

    workbook = new XSSFWorkbook();
    org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
    sheetName = sheet.getSheetName();
    sheet.setColumnWidth(0, 5000);
    sheet.setColumnWidth(1, 5000);
    sheet.setColumnWidth(2, 5000);
    sheet.setColumnWidth(3, 5000);
    sheet.setColumnWidth(4, 6000);
    sheet.setColumnWidth(5, 6700);
    sheet.setColumnWidth(6, 5000);
    sheet.setColumnWidth(7, 5000);

    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setAlignment(HorizontalAlignment.CENTER);

    Row header = sheet.createRow(0);
    header.setRowStyle(cellStyle);
    Cell headerCell = header.createCell(0);
    headerCell.setCellValue("Timestamp");
    headerCell = header.createCell(1);
    headerCell.setCellValue("DC input 1 in KW");
    headerCell = header.createCell(2);
    headerCell.setCellValue("DC input 2 in KW");
    headerCell = header.createCell(3);
    headerCell.setCellValue("Battery charge in %");
    headerCell = header.createCell(4);
    headerCell.setCellValue("Consumption from PV in KW");
    headerCell = header.createCell(5);
    headerCell.setCellValue("Consumption from Battery in KW");
    headerCell = header.createCell(6);
    headerCell.setCellValue("Grid Purchase in KW");
    headerCell = header.createCell(7);
    headerCell.setCellValue("Grid Feed-In in KW");
  }
}
