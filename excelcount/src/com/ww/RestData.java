package com.ww;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @description:RestData
 * @author:pxf
 * @data:2023/10/07
 **/
public class RestData {
    public static JSONObject getData(XSSFSheet sheetParam, String nameParam, LocalDate beginDateParam, LocalDate endDateParam)  {
        JSONObject resultJSON = new JSONObject(3);
        InputStream inputStream = CountDate.class.getClassLoader().getResourceAsStream("rest.xlsx");
        XSSFWorkbook workbook = null;
        double ttCount = 0.00;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
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
