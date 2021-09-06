package com.example.test.utils.plugins.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
public class ExportXlsxUtil {

    public static String createExcelFile(List<Map<String, String>> headers, List<Map<String, Object>> datas, String caption, String explain) throws Exception {

        SXSSFWorkbook workbook = new SXSSFWorkbook(10000);
        Sheet sheet = workbook.createSheet("data");

        Entry<String, String> header = null;
        String[] titles = null;
        List<ExcelHeader> excelHeaders = new ArrayList<ExcelHeader>();
        int total_cc = 1;
        for (int i = 0, len = headers.size(); i < len; i++) {
            String id = "";
            int cc = 0;
            header = headers.get(i).entrySet().iterator().next();
            titles = header.getKey().split("\\.");
            for (int index = 0, ilen = titles.length; index < ilen; index++) {
                ExcelHeader excelHeader = new ExcelHeader();
                excelHeader.setParent(id);
                id += ("".equals(id) ? "" : ".") + titles[index];
                excelHeader.setId(id);
                excelHeader.setText(titles[index]);
                excelHeader.setCloumn(index < ilen - 1 ? "" : header.getValue());
                if (!addExcelHeader(excelHeaders, excelHeader)) {
                    throw new Exception("excelHeaders is error ...");
                }
                cc++;
            }
            total_cc = total_cc > cc ? total_cc : cc;
        }

        List<String> cloumns = parseExcelHeader(excelHeaders, total_cc, 0);

        int lastRowIndex = 0;

        if (caption != null && !"".equals(caption)) {
            CellStyle captionStyle = workbook.createCellStyle();
            Font captionFont = workbook.createFont();
            captionFont.setColor(XSSFFont.COLOR_NORMAL);
            captionFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
            captionFont.setFontHeightInPoints((short) 18);    //设置字体大小
            captionStyle.setFont(captionFont);
            captionStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
            captionStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 上下居中
            lastRowIndex = buildCaption(captionStyle, sheet, lastRowIndex, cloumns.size(), caption) + 1;
        }

        if (explain != null && !"".equals(explain)) {
            CellStyle explainStyle = workbook.createCellStyle();
            Font explainFont = workbook.createFont();
            explainFont.setColor(XSSFFont.COLOR_NORMAL);
            explainFont.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);// 加粗
            explainFont.setFontHeightInPoints((short) 12);    //设置字体大小
            explainStyle.setFont(explainFont);
            explainStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);// 左右居中
            explainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 上下居中
            lastRowIndex = buildExplain(explainStyle, sheet, lastRowIndex, cloumns.size(), explain) + 1;
        }

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setColor(XSSFFont.COLOR_NORMAL);
        headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
        headerFont.setFontHeightInPoints((short) 12);    //设置字体大小
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
        headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 上下居中
        headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        headerStyle.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
        headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        lastRowIndex = buildExcelHeader(headerStyle, sheet, lastRowIndex, 0, excelHeaders);


        CellStyle dataStyle = workbook.createCellStyle();
        Font dataFont = workbook.createFont();
        dataFont.setColor(XSSFFont.COLOR_NORMAL);
        dataFont.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);// 加粗
        dataFont.setFontHeightInPoints((short) 10);    //设置字体大小
        dataStyle.setFont(dataFont);
        dataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
        dataStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 上下居中
        dataStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

        lastRowIndex = buildExcelData(dataStyle, sheet, lastRowIndex + 1, 0, cloumns, datas);
        FileOutputStream fout = null;
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".xlsx";
        File file = null;
        try {
            file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fout = new FileOutputStream(file);
            workbook.write(fout);
            fout.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                fout.close();
            }
        }
        return fileName;
    }


    private static int buildCaption(CellStyle captionStyle, Sheet sheet, int rowIndex, int rangeCount, String caption) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        row.setHeight((short) 1000);
        Cell cell = row.getCell(0);
        if (cell == null) {
            cell = row.createCell(0);
        }
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0, rangeCount - 1);
        sheet.addMergedRegion(region);
        setRegionStyle(sheet, region, captionStyle);

        cell.setCellValue(caption);

        return rowIndex;
    }

    private static int buildExplain(CellStyle captionStyle, Sheet sheet, int rowIndex, int rangeCount, String explain) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        row.setHeight((short) 400);
        Cell cell = row.getCell(0);
        if (cell == null) {
            cell = row.createCell(0);
        }
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0, rangeCount - 1);
        sheet.addMergedRegion(region);
        setRegionStyle(sheet, region, captionStyle);

        cell.setCellValue(explain);

        return rowIndex;
    }

    private static int buildExcelHeader(CellStyle headerStyle, Sheet sheet, int rowIndex, int cellIndex, List<ExcelHeader> excelHeaders) {
        int rRowIndex = rowIndex + 0;
        for (int i = 0, len = excelHeaders.size(); i < len; i++) {
            int temp_cellIndex = cellIndex + 0;

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            Cell cell = row.getCell(cellIndex);
            if (cell == null) {
                cell = row.createCell(cellIndex);
            }

            sheet.setColumnWidth(cellIndex, 3000);// 设置列宽

            ExcelHeader excelHeader = excelHeaders.get(i);

            CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex + excelHeader.getRowspan() - 1, cellIndex, cellIndex + excelHeader.getColspan() - 1);
            sheet.addMergedRegion(region);
            setRegionStyle(sheet, region, headerStyle);

            cell.setCellValue(excelHeader.getText());

            cellIndex += excelHeader.getColspan();

            if (excelHeader.getChildren().size() > 0) {
                rRowIndex = buildExcelHeader(headerStyle, sheet, rowIndex + 1, temp_cellIndex, excelHeader.getChildren());
                rRowIndex = rRowIndex > rowIndex ? rRowIndex : rowIndex;
            }
        }
        return rRowIndex;
    }

    private static int buildExcelData(CellStyle dataStyle, Sheet sheet, int rowIndex, int cellIndex, List<String> cloumns, List<Map<String, Object>> datas) {
        if (datas != null && datas.size() > 0 && cloumns != null && cloumns.size() > 0) {
            Row row;
            Cell cell;
            int temp_cellIndex;
            Map<String, Object> data;
            Object cellValue = null;
            for (int i = 0, len = datas.size(); i < len; i++) {
                data = datas.get(i);
                temp_cellIndex = cellIndex + 0;
                row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                for (int j = 0, jlen = cloumns.size(); j < jlen; j++) {
                    cell = row.getCell(temp_cellIndex);
                    if (cell == null) {
                        cell = row.createCell(temp_cellIndex);
                    }
                    cell.setCellStyle(dataStyle);
                    cellValue = data.get(cloumns.get(j));
                    if (cellValue != null) {
                        try {
//							cell.setCellValue(Double.parseDouble(cellValue+""));
                            cell.setCellValue(cellValue + "");
                        } catch (Exception e) {
                            cell.setCellValue(cellValue + "");
                        }
                    }
                    temp_cellIndex++;
                }
                rowIndex++;
            }
            log.info("total data count:" + rowIndex);
        }
        return rowIndex;
    }

    private static void setRegionStyle(Sheet sheet, CellRangeAddress region, CellStyle cs) {
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            for (int j = region.getFirstColumn(); j <= region
                    .getLastColumn(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(cs);
            }
        }
    }

    private static List<String> parseExcelHeader(List<ExcelHeader> excelHeaders, int total_cc, int cc) {
        List<String> cloumns = new ArrayList<String>();
        for (int i = 0, len = excelHeaders.size(); i < len; i++) {
            ExcelHeader excelHeader = excelHeaders.get(i);
            if (excelHeader.getCloumn() != null && !"".equals(excelHeader.getCloumn())) {
                cloumns.add(excelHeader.getCloumn());
            }
            if (!(excelHeader.getChildren().size() > 0)) {
                excelHeader.setRowspan(total_cc - cc);

            } else {
                excelHeader.setColspan(excelHeader.getChildren().size());
                cloumns.addAll(parseExcelHeader(excelHeader.getChildren(), total_cc, cc + 1));
            }
        }
        return cloumns;
    }

    private static boolean addExcelHeader(List<ExcelHeader> excelHeaders, ExcelHeader excelHeader) {
        boolean b = false;
        if (excelHeader.getParent() == null || "".equals(excelHeader.getParent())) {
            for (ExcelHeader header : excelHeaders) {
                if (header.getId().equals(excelHeader.getId())) {
                    b = true;
                }
            }
            if (!b) {
                excelHeaders.add(excelHeader);
                b = true;
            }
            return b;
        } else {
            for (ExcelHeader parent : excelHeaders) {
                if (parent.getId().equals(excelHeader.getParent())) {
                    parent.getChildren().add(excelHeader);
                    b = true;
                    return b;
                } else {
                    if (parent.getChildren().size() > 0) {
                        b = addExcelHeader(parent.getChildren(), excelHeader);
                        if (b) {
                            return b;
                        }
                    }
                }
            }
        }
        return b;
    }

    public static Map<String, String> getHeaderMap(String key, String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    }

    public static void main(String[] args) {

        List<Map<String, String>> headers = new ArrayList<Map<String, String>>();
        headers.add(getHeaderMap("工业公司", "V_LEGAL_NAME"));
        headers.add(getHeaderMap("价类", "V_PRICE_TYPE"));
        headers.add(getHeaderMap("产量.本期", "V_PRO_Y_NUM"));
        headers.add(getHeaderMap("产量.同期", "V_PRO_Y_NUM_T"));
        headers.add(getHeaderMap("产量.增幅%", "V_PRO_Y_RATIO"));
        headers.add(getHeaderMap("销量.本期", "V_DB_Y_NUM"));
        headers.add(getHeaderMap("销量.同期", "V_DB_Y_NUM_T"));
        headers.add(getHeaderMap("销量.增幅%", "V_DB_Y_RATIO"));
        headers.add(getHeaderMap("期末库存.本期", "V_KC"));
        headers.add(getHeaderMap("期末库存.同期", "V_KC_T"));
        headers.add(getHeaderMap("期末库存.增幅%", "V_KC_RATIO"));

        try {
            System.out.println(ExportXlsxUtil.createExcelFile(headers, null, "测试标题", "测试说明"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
