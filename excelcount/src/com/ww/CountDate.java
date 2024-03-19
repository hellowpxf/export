package com.ww;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

/**
 * @description:CountDate
 * @author:pxf
 * @data:2023/09/28
 **/
public class CountDate {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = CountDate.class.getClassLoader().getResourceAsStream("table.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("合计");
        for (int i=2 ;sheet.getRow(i) != null ; i++) {
            int tCount = 0;
            int decCount = 0;
            Row beginRow = sheet.getRow(i);
            Row endRow = sheet.getRow(i);
            String beginDate = beginRow.getCell(4).getStringCellValue();
            String endDate =   endRow.getCell(5).getStringCellValue();
            if (beginDate == null || endDate == null || beginDate == ""  || endDate == ""  ){
                break;
            }

            LocalDate startDateL = LocalDate.parse(beginDate);
            LocalDate endDateL = LocalDate.parse(endDate);
            long daysBetween = ChronoUnit.DAYS.between(startDateL, endDateL)+1;
            //总表数据
            beginRow.createCell(6).setCellValue(daysBetween);
            //分表
            String startYearAndMonth = null;
            if (startDateL.getMonthValue()<10){
                startYearAndMonth =  startDateL.getYear()+"0"+startDateL.getMonthValue();
            }else {
                startYearAndMonth =  startDateL.getYear()+""+startDateL.getMonthValue();
            }
            String endYearAndMonth = null;
            if (startDateL.getMonthValue()<10){
                endYearAndMonth =  endDateL.getYear()+"0"+endDateL.getMonthValue();
            }else {
                endYearAndMonth =  endDateL.getYear()+""+endDateL.getMonthValue();
            }
            XSSFSheet loopSheet ;
            //每个月份一个sheet
                while (Integer.parseInt(startYearAndMonth)<=Integer.parseInt(endYearAndMonth)) {
                    loopSheet = workbook.getSheet(startYearAndMonth);
                    if (loopSheet == null) {
                        workbook.createSheet(startYearAndMonth);
                    }else {
                        //填充标题和副标题
                        setExcelValue(workbook,loopSheet,sheet,startYearAndMonth);
                    }
                    startDateL = startDateL.plusMonths(1);
                    if (startDateL.getMonthValue() < 10) {
                        startYearAndMonth = startDateL.getYear() + "0" + startDateL.getMonthValue();
                    } else {
                        startYearAndMonth = startDateL.getYear() + "" + startDateL.getMonthValue();
                    }
                }
            //
            startDateL = LocalDate.parse(beginDate);
            endDateL = LocalDate.parse(endDate);
            boolean isCrossMonth = startDateL.getMonthValue() == endDateL.getMonthValue()
                    && startDateL.getYear() == endDateL.getYear();
            //分表
            while (!isCrossMonth){
                LocalDate lastDayOfMonth =  YearMonth.of(startDateL.getYear(), startDateL.getMonth()).atEndOfMonth();
                if (startDateL.getMonthValue() < 10) {
                    startYearAndMonth = startDateL.getYear() + "0" + startDateL.getMonthValue();
                } else {
                    startYearAndMonth = startDateL.getYear() + "" + startDateL.getMonthValue();
                }
                loopSheet = workbook.getSheet(startYearAndMonth);
                int subRowNum = getLastRowNum(loopSheet);
                Row subBeginRow = loopSheet.createRow(subRowNum);
                beginRow = sheet.getRow(i);
                setExcelValue(workbook,loopSheet,subBeginRow,beginRow,startDateL,lastDayOfMonth);
                decCount += setNoteValue(workbook,loopSheet, subBeginRow,beginRow, startDateL, lastDayOfMonth);
                tCount += getDayOfMonth(lastDayOfMonth);
                startDateL = startDateL.plusMonths(1);
                isCrossMonth = startDateL.getMonthValue() == endDateL.getMonthValue()
                        && startDateL.getYear() == endDateL.getYear();


            }
            if (startDateL.getMonthValue() < 10) {
                startYearAndMonth = startDateL.getYear() + "0" + startDateL.getMonthValue();
            } else {
                startYearAndMonth = startDateL.getYear() + "" + startDateL.getMonthValue();
            }
            loopSheet = workbook.getSheet(startYearAndMonth);
            int subRowNum = getLastRowNum(loopSheet);
            Row subBeginRow = loopSheet.createRow(subRowNum);
            beginRow = sheet.getRow(i);
            setExcelValue(workbook,loopSheet,subBeginRow,beginRow,startDateL,endDateL);
            tCount += getDayOfMonth(endDateL);
            decCount += setNoteValue(workbook,loopSheet, subBeginRow,beginRow, startDateL, endDateL);
            double tday = beginRow.getCell(6).getNumericCellValue();
            beginRow.createCell(6).setCellValue(tday-(decCount));
            beginRow.createCell(7).setCellValue(tCount-(decCount*10));
            setContentStyleScope(workbook,beginRow,0,7);
            setContentStyleScope(workbook,subBeginRow,0,8);
            System.out.println(decCount+"==================");
            decCount=0;
            tCount =0;

        }
        //求和
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int j = 0; j <numberOfSheets ; j++) {
            XSSFSheet sheetSum = workbook.getSheetAt(j);
            int lastSumNum = getLastRowNum(sheetSum);
            Row lastSumRow = sheetSum.createRow(lastSumNum);
            String currentFormula = null;
            sheetSum.setColumnWidth(lastSumNum, 10);
            currentFormula = "SUM(H3:H" + lastSumNum + ")";
            lastSumRow.createCell(7).setCellFormula(currentFormula);
            sheetSum.addMergedRegion(new CellRangeAddress(lastSumNum, lastSumNum, 0, 6));
            lastSumRow.createCell(0).setCellValue("合计");
            setColumnSizeAuto(sheetSum,8,8);
        }

        // 启用自动计算
        ((XSSFWorkbook)workbook).setForceFormulaRecalculation(true);
        try (FileOutputStream outputStream = new FileOutputStream("C:\\example.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 表格赋值
     * @param subBeginRow
     * @param beginRow
     * @param startDateL
     * @param endDateL
     */
    public  static  void setExcelValue(XSSFWorkbook workbook,XSSFSheet loopSheet, Row subBeginRow,
                                       Row beginRow,LocalDate startDateL,LocalDate endDateL){
        subBeginRow.createCell(0).setCellValue(beginRow.getCell(0).getNumericCellValue());
        subBeginRow.createCell(1).setCellValue(beginRow.getCell(1).getStringCellValue());
        subBeginRow.createCell(2).setCellValue(beginRow.getCell(2).getStringCellValue());
        subBeginRow.createCell(3).setCellValue(beginRow.getCell(3).getStringCellValue());
        subBeginRow.createCell(4).setCellValue(startDateL.toString());
        subBeginRow.createCell(5).setCellValue(endDateL.toString());
        long subBetween = ChronoUnit.DAYS.between(startDateL, endDateL)+1;
        subBeginRow.createCell(6).setCellValue(subBetween);
        int  day = getDayOfMonth(endDateL);
        subBeginRow.createCell(7).setCellValue(day);
        subBeginRow.createCell(8).setCellValue("");
        setContentStyleScope(workbook,subBeginRow,0,8);
    }

    /**
     * 表格赋值
     * @param loopSheet
     * @param sheet
     */
    public  static  void setExcelValue(XSSFWorkbook workbook,XSSFSheet loopSheet, XSSFSheet sheet,String month){
        if (loopSheet.getRow(0)==null){
            loopSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
            String top1 = sheet.getRow(0).getCell(0).getStringCellValue();
            top1 = top1.substring(0,top1.length()-3)+"("+month+")";
            loopSheet.createRow(0).createCell(0).setCellValue(top1);
            //格式设置
            Row subBeginRow =  loopSheet.createRow(1);
            Row beginRow =  sheet.getRow(1);
            setHeaderStyle(workbook,loopSheet.getRow(0).getCell(0));
            subBeginRow.createCell(0).setCellValue(beginRow.getCell(0).getStringCellValue());
            subBeginRow.createCell(1).setCellValue(beginRow.getCell(1).getStringCellValue());
            subBeginRow.createCell(2).setCellValue(beginRow.getCell(2).getStringCellValue());
            subBeginRow.createCell(3).setCellValue(beginRow.getCell(3).getStringCellValue());
            subBeginRow.createCell(4).setCellValue(beginRow.getCell(4).getStringCellValue());
            subBeginRow.createCell(5).setCellValue(beginRow.getCell(5).getStringCellValue());
            subBeginRow.createCell(6).setCellValue(beginRow.getCell(6).getStringCellValue());
            subBeginRow.createCell(7).setCellValue(beginRow.getCell(7).getStringCellValue());
            subBeginRow.createCell(8).setCellValue("备注");
            setHeaderStyleScope(workbook,subBeginRow,0,8);

        }
    }

    /**
     * 获取sheet页最后一行
     * @param loopSheet
     * @return
     */
    public  static  int getLastRowNum( XSSFSheet loopSheet){
        int subRowNum =2;
        Row row = loopSheet.getRow(2);
        while (row != null){
            subRowNum++;
            row = loopSheet.getRow(subRowNum);
        }
        return  subRowNum;
    }

    public static int getDayOfMonth(LocalDate endDateL){
        if("2".equals(String.valueOf(endDateL.getMonthValue()))&&

                "28".equals(String.valueOf(endDateL.getDayOfMonth()))){
            return  300;
        }else if("31".equals(String.valueOf(endDateL.getDayOfMonth()))) {
            return  300;
        }else{
            return endDateL.getDayOfMonth()*10;
        }
    }

    public  static  int  setNoteValue(XSSFWorkbook workbook, XSSFSheet loopSheet,
                                      Row subBeginRow, Row beginRow,LocalDate startDateL,LocalDate endDateL){
        String name  = beginRow.getCell(1).getStringCellValue();
        int  personNum =  (int)beginRow.getCell(0).getNumericCellValue();
        String numAndName = personNum+"."+name;
        JSONObject dataObj = RestData.getData(loopSheet, numAndName, startDateL, endDateL);
        if (dataObj == null){
            return 0;
        }
        String note = dataObj.getString("note");
        double mineMoney = dataObj.getDoubleValue("money");
        double dayCount = dataObj.getDoubleValue("day");
        double money = subBeginRow.getCell(7).getNumericCellValue();
        double day = subBeginRow.getCell(6).getNumericCellValue();
        if (mineMoney>0.00){
            subBeginRow.getCell(6).setCellValue(day-dayCount);
            subBeginRow.createCell(7).setCellValue(money-mineMoney);
            subBeginRow.getCell(8).setCellValue(note);
        }
        setContentStyleScope(workbook,subBeginRow,0,8);
        return (int)dayCount;
    }

    /**
     * 单元格格式设置
     * @param workbook
     * @param cell
     */
    public static void setHeaderStyle(XSSFWorkbook workbook, XSSFCell cell){
        CellStyle style = getHeaderCellStyle(workbook);
        cell.setCellStyle(style);
    }

    /**
     * 批量设置单元格格式
     * @param workbook
     * @param rows
     * @param begin
     * @param end
     */
    public static void setHeaderStyleScope(XSSFWorkbook workbook, Row rows,int begin,int end){
        CellStyle style = getHeaderCellStyle(workbook);
        for (; begin <= end ; begin++) {
            rows.getCell(begin).setCellStyle(style);
        }
    }

    /**
     * 样式
     * @param workbook
     * @return
     */
    public static CellStyle getHeaderCellStyle(XSSFWorkbook workbook){
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        XSSFFont font = workbook.createFont();
        style.setAlignment(CENTER);
        font.setBold(true);
        style.setFont(font);
        return  style;
    }
    /**
     * 样式
     * @param workbook
     * @return
     */
    public static CellStyle getContentStyle(XSSFWorkbook workbook){
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return  style;
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
     * 自动调整单元格大小
     * @param sheet
     * @param begin
     * @param end
     */
    public static  void setColumnSizeAuto(XSSFSheet sheet,int begin,int end){
        for (; begin <= end ; begin++) {
            sheet.autoSizeColumn(begin);
        }
    }
}
