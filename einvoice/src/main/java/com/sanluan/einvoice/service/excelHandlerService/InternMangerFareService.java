package com.sanluan.einvoice.service.excelHandlerService;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;

/**
 * @description:管理费
 * @author:pxf
 * @data:2023/10/09
 **/
@Service
public class InternMangerFareService {
    public Object internExcelHandler(List<MultipartFile> files, HttpServletResponse response) throws IOException, InvalidFormatException {
        MultipartFile file = files.get(0);
        MultipartFile file2 = files.get(1);
        File resFileDest = null;
        File dest = null;
        String fileName = file.getOriginalFilename();
        String file2Name = file2.getOriginalFilename();
        if (null != file && !file.isEmpty()) {
            try {
                if ("休假报表.xlsx".equals(fileName)){
                    dest = new File(file2Name);
                    resFileDest = new File(fileName);
                    FileUtils.copyInputStreamToFile(file2.getInputStream(), dest);
                    FileUtils.copyInputStreamToFile(file.getInputStream(), resFileDest);
                }else {
                    dest = new File(fileName);
                    resFileDest = new File(file2Name);
                    FileUtils.copyInputStreamToFile(file2.getInputStream(), resFileDest);
                    FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
                }
            } catch (IOException e) {
            }
        }

        XSSFWorkbook workbook = new XSSFWorkbook(dest);
        XSSFSheet sheet = workbook.getSheet("合计");
        for (int i = 2; sheet.getRow(i) != null; i++) {
            int tCount = 0;
            int decCount = 0;
            Row beginRow = sheet.getRow(i);
            Row endRow = sheet.getRow(i);
            String beginDate = beginRow.getCell(4).getStringCellValue();
            String endDate = endRow.getCell(5).getStringCellValue();
            if (beginDate == null || endDate == null || beginDate == "" || endDate == "") {
                break;
            }

            LocalDate startDateL = LocalDate.parse(beginDate);
            LocalDate endDateL = LocalDate.parse(endDate);
            long daysBetween = ChronoUnit.DAYS.between(startDateL, endDateL) + 1;
            //总表数据
            beginRow.createCell(6).setCellValue(daysBetween);
            //分表
            String startYearAndMonth = null;
            if (startDateL.getMonthValue() < 10) {
                startYearAndMonth = startDateL.getYear() + "0" + startDateL.getMonthValue();
            } else {
                startYearAndMonth = startDateL.getYear() + "" + startDateL.getMonthValue();
            }
            String endYearAndMonth = null;
            if (startDateL.getMonthValue() < 10) {
                endYearAndMonth = endDateL.getYear() + "0" + endDateL.getMonthValue();
            } else {
                endYearAndMonth = endDateL.getYear() + "" + endDateL.getMonthValue();
            }
            XSSFSheet loopSheet;
            //每个月份一个sheet
            while (Integer.parseInt(startYearAndMonth) <= Integer.parseInt(endYearAndMonth)) {
                loopSheet = workbook.getSheet(startYearAndMonth);
                if (loopSheet == null) {
                    workbook.createSheet(startYearAndMonth);
                } else {
                    //填充标题和副标题
                    setExcelValue(workbook, loopSheet, sheet, startYearAndMonth);
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
            while (!isCrossMonth) {
                LocalDate lastDayOfMonth = YearMonth.of(startDateL.getYear(), startDateL.getMonth()).atEndOfMonth();
                if (startDateL.getMonthValue() < 10) {
                    startYearAndMonth = startDateL.getYear() + "0" + startDateL.getMonthValue();
                } else {
                    startYearAndMonth = startDateL.getYear() + "" + startDateL.getMonthValue();
                }
                loopSheet = workbook.getSheet(startYearAndMonth);
                int subRowNum = getLastRowNum(loopSheet);
                Row subBeginRow = loopSheet.createRow(subRowNum);
                beginRow = sheet.getRow(i);
                setExcelValue(workbook, loopSheet, subBeginRow, beginRow, startDateL, lastDayOfMonth);
                decCount += setNoteValue(resFileDest, workbook, loopSheet, subBeginRow, beginRow, startDateL, lastDayOfMonth);
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
            setExcelValue(workbook, loopSheet, subBeginRow, beginRow, startDateL, endDateL);
            tCount += getDayOfMonth(endDateL);
            decCount += setNoteValue(resFileDest,workbook, loopSheet, subBeginRow, beginRow, startDateL, endDateL);
            double tday = beginRow.getCell(6).getNumericCellValue();
            beginRow.createCell(6).setCellValue(tday - (decCount));
            beginRow.createCell(7).setCellValue(tCount - (decCount * 10));
            setContentStyleScope(workbook, beginRow, 0, 7);
            setContentStyleScope(workbook, subBeginRow, 0, 8);
            System.out.println(decCount + "==================");
            decCount = 0;
            tCount = 0;

        }
        //求和
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int j = 0; j < numberOfSheets; j++) {
            XSSFSheet sheetSum = workbook.getSheetAt(j);
            int lastSumNum = getLastRowNum(sheetSum);
            Row lastSumRow = sheetSum.createRow(lastSumNum);
            String currentFormula = null;
            sheetSum.setColumnWidth(lastSumNum, 10);
            currentFormula = "SUM(H3:H" + lastSumNum + ")";
            lastSumRow.createCell(7).setCellFormula(currentFormula);
            sheetSum.addMergedRegion(new CellRangeAddress(lastSumNum, lastSumNum, 0, 6));
            lastSumRow.createCell(0).setCellValue("合计");
            setColumnSizeAuto(sheetSum, 8, 8);
        }
        setResponseHeader(response,"f.xlsx");
        // 启用自动计算
        ((XSSFWorkbook) workbook).setForceFormulaRecalculation(true);
        //声明输出流
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            workbook.write(os);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            os.close();
            return  false;
        }
        return  true;
    }

    /** 设置浏览器下载响应头
     */
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public  static  int  setNoteValue(File file, XSSFWorkbook workbook, XSSFSheet loopSheet,
                                      Row subBeginRow, Row beginRow,LocalDate startDateL,LocalDate endDateL){
        String name  = beginRow.getCell(1).getStringCellValue();
        int  personNum =  (int)beginRow.getCell(0).getNumericCellValue();
        String numAndName = personNum+"."+name;
        JSONObject dataObj = RestData.getData(file, loopSheet, numAndName, startDateL, endDateL);
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

class RestData {
        public static JSONObject getData(File file, XSSFSheet sheetParam, String nameParam, LocalDate beginDateParam, LocalDate endDateParam)  {
            JSONObject resultJSON = new JSONObject(3);

            XSSFWorkbook workbook = null;
            double ttCount = 0.00;
            try {
                workbook = new XSSFWorkbook(file);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
            XSSFSheet sheet = workbook.getSheet("sheet1");
            StringBuffer sb = new StringBuffer();
            double decMoney = 0.00;
            int countDay = 0;
            for (int i = 0; sheet.getRow(i) != null ; i++) {
                Row beginRow = sheet.getRow(i);
                Row endRow = sheet.getRow(i);
                Cell nameCell  =  beginRow.getCell(1);
                Cell typeCell  =  beginRow.getCell(9);
                if (endRow == null || nameCell ==  null || typeCell==null){
                    continue;
                }
                String name = beginRow.getCell(1).getStringCellValue();
                String restType = endRow.getCell(9).getStringCellValue();
                if ("WX.无薪假".equals(restType) && name != null && name.length()>0 && name.equals(nameParam)){
                    String beginDate = endRow.getCell(13).getStringCellValue();
                    String endDate = endRow.getCell(14).getStringCellValue();
                    LocalDate startDateL = LocalDate.parse(beginDate);
                    LocalDate endDateL = LocalDate.parse(endDate);
                    //如果开始时间和结束时间都在同一个月
                    String strStartDate = startDateL.getYear() + "-" + startDateL.getMonthValue();
                    String strEndDate = endDateL.getYear() + "-" + endDateL.getMonthValue();
                    String strStartDateParam = beginDateParam.getYear() + "-" + beginDateParam.getMonthValue();
                    //如果年月相同直接相加
                   /* if (strStartDate.equals(strEndDate) && strEndDate.equals(strStartDateParam) ){
                        double hour = endRow.getCell(16).getNumericCellValue();
                        System.out.println("+jjj+"+hour);
                         hour = hour/8.00;
                         if (hour>3){
                             resultJSON.put("money",(hour-3)*10.00);
                             resultJSON.put("note",startDateL.toString()+"到"+endDateL.toString()+"休无薪假");
                         }
                     //如果请假跨月，当前月份在请假开始月份
                    }else if(strStartDate.equals(strStartDateParam) && !strEndDate.equals(strStartDateParam) ){
                        double hour =(double) ChronoUnit.DAYS.between(startDateL, endDateParam)+1;
                        if (hour>3){
                            resultJSON.put("money",(hour-3)*10.00);
                            resultJSON.put("note",startDateL.toString()+"到"+endDateParam.toString()+"休无薪假");
                        }
                     //如果请假跨月，当前月份在请假结束月份
                    }else if (strEndDate.equals(strStartDateParam) && !strStartDate.equals(strStartDateParam)){
                        double hour =(double) ChronoUnit.DAYS.between(beginDateParam, endDateL)+1;
                        if (hour>3){
                            resultJSON.put("money",(hour-3)*10.00);
                            resultJSON.put("note",beginDateParam.toString()+"到"+endDateL.toString()+"休无薪假");
                        }
                    }*/
                    ttCount += (endRow.getCell(16).getNumericCellValue())/8.00;
                    if (strStartDate.equals(strEndDate) && strEndDate.equals(strStartDateParam) ){
                        double hour = endRow.getCell(16).getNumericCellValue();
                        hour = hour/8.00;
                        countDay += hour;
                        decMoney += hour*10.00;
                        sb.append(startDateL.toString()+"到"+endDateL.toString()+"休无薪假"+hour+"天；");
                        //如果请假跨月，当前月份在请假开始月份
                    }else if(strStartDate.equals(strStartDateParam) && !strEndDate.equals(strStartDateParam) ){
                        double hour =(double) ChronoUnit.DAYS.between(startDateL, endDateParam)+1;
                        countDay += hour;
                        decMoney += hour*10.00;
                        sb.append(startDateL.toString()+"到"+endDateParam.toString()+"休无薪假"+hour+"天；");
                        //如果请假跨月，当前月份在请假结束月份
                    }else if (strEndDate.equals(strStartDateParam) && !strStartDate.equals(strStartDateParam)){
                        double hour =(double) ChronoUnit.DAYS.between(beginDateParam, endDateL)+1;
                        decMoney += hour*10.00;
                        countDay += hour;
                        sb.append(beginDateParam.toString()+"到"+endDateL.toString()+"休无薪假"+hour+"天；");
                    }
                    resultJSON.put("money",decMoney);
                    resultJSON.put("note",sb);
                    resultJSON.put("day",countDay);
                }
            }
            if(ttCount>3) {

                return resultJSON;
            }else {
                return  null;
            }
        }

}
