package com.ww;

import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @description:CountTaxData
 * @author:pxf
 * @data:2023/10/08
 **/
public class CountTaxData {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = CountDate.class.getClassLoader().getResourceAsStream("tax.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("sheet1");
        ArrayList<JSONObject> taxObjectList = new ArrayList<>();
        int j =0;
        for (int i = 7; sheet.getRow(i) != null; i++) {
            JSONObject taxMessage = new JSONObject();
            Row row = sheet.getRow(i);
            String beginDate = row.getCell(1).getStringCellValue();
            String invoiceNo = row.getCell(3).getStringCellValue();
            String from = row.getCell(4).getStringCellValue();
            String to = row.getCell(7).getStringCellValue();
            Double fare = row.getCell(24).getNumericCellValue();
            if (beginDate == null || "".equals(beginDate)){
                beginDate =  taxObjectList.get(j-1).getString("date");
            }
            if (fare >0 && from != null && !"".equals(from)) {
                taxMessage.put("date",beginDate);
                taxMessage.put("invoiceNo",invoiceNo);
                taxMessage.put("from",from);
                taxMessage.put("to",to);
                taxMessage.put("fare",fare);
                taxObjectList.add(taxMessage);
                j++;
            }
        }
        System.out.println(taxObjectList.toString());

        //内容填充
        XSSFSheet sheet2 = workbook.createSheet("sheet22");
        XSSFRow row3 = sheet2.createRow(3);
        XSSFRow row4 = sheet2.createRow(4);
        XSSFRow row5 = sheet2.createRow(5);
        XSSFRow row6 = sheet2.createRow(6);
        XSSFRow row7 = sheet2.createRow(7);
        XSSFRow row8 = sheet2.createRow(8);
        XSSFRow row9 = sheet2.createRow(9);
        row9.createCell(1).setCellValue(getIte());
        row3.createCell(0).setCellValue("Invoice No.:");
        row4.createCell(0).setCellValue("Date:");
        row5.createCell(0).setCellValue("Time：");
        row6.createCell(0).setCellValue("Amount：");
        row7.createCell(0).setCellValue("From:");
        row8.createCell(0).setCellValue(" To:");
        int k = 1;
        CellStyle style = getContentStyle(workbook);
        for (int i = 0; i <taxObjectList.size() ; i++) {
            Cell cell3 = row3.createCell(k);
            Cell cell4 = row4.createCell(k);
            Cell cell5 = row5.createCell(k);
            Cell cell6 = row6.createCell(k);
            Cell cell7 = row7.createCell(k);
            Cell cell8 = row8.createCell(k);
            cell3.setCellValue(taxObjectList.get(i).getString("invoiceNo"));
            cell4.setCellValue(taxObjectList.get(i).getString("date"));
            cell5.setCellValue("");
            cell6.setCellValue(taxObjectList.get(i).getString("fare"));
            cell7.setCellValue(taxObjectList.get(i).getString("from"));
            cell8.setCellValue(taxObjectList.get(i).getString("to"));


            cell3.setCellStyle(style);
            cell4.setCellStyle(style);
            cell5.setCellStyle(style);
            cell6.setCellStyle(style);
            cell7.setCellStyle(style);
            cell8.setCellStyle(style);

            Cell  cellCopy3 = row3.createCell(k+1);
            Cell  cellCopy4 = row4.createCell(k+1);
            Cell  cellCopy5 = row5.createCell(k+1);
            Cell  cellCopy6 = row6.createCell(k+1);
            Cell  cellCopy7 = row7.createCell(k+1);
            Cell  cellCopy8 = row8.createCell(k+1);

            cellCopy3.setCellValue("");
            cellCopy4.setCellValue("");
            cellCopy5.setCellValue("");
            cellCopy6.setCellValue("");
            cellCopy7.setCellValue("");
            cellCopy8.setCellValue("");
            cellCopy3.setCellStyle(style);
            cellCopy4.setCellStyle(style);
            cellCopy5.setCellStyle(style);
            cellCopy6.setCellStyle(style);
            cellCopy7.setCellStyle(style);
            cellCopy8.setCellStyle(style);
            k +=3;
        }
        FileOutputStream outputStream = new FileOutputStream("C:\\example.xlsx");
        workbook.write(outputStream);
    }
    /**
     * 内容单元格格式批量设置
     * @param workbook
     * @param rows
     * @param begin
     * @param end
     */
    public static void setContentStyleScope(XSSFWorkbook workbook, Row rows,int begin,int end){
        CellStyle style = getContentStyle(workbook);
        for (; begin <= end ; begin++) {
            rows.getCell(begin).setCellStyle(style);
        }
    }

    /**
     * 样式
     * @param workbook
     * @return
     */
    public static CellStyle getContentStyle(XSSFWorkbook workbook){
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        return  style;
    }
    public static String getIte() {
        // 指定行程单的PDF文件路径
        String pdfFilePath = "C:/Users/Administrator/Desktop/发票/xcd2.pdf";

        try {
            // 加载PDF文档
            PDDocument document = PDDocument.load(new File(pdfFilePath));

            // 使用PDF文本提取器提取文本
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // 输出提取的文本
            System.out.println("行程单内容：");
            System.out.println(text);
            // 关闭文档
            document.close();
            return text;
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
