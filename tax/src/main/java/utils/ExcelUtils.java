package utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Workbook;
import tax.Tax;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:ExcelUtils
 * @author:pxf
 * @data:2023/04/27
 **/
public class ExcelUtils {

    /**
     * 设置表头
     */
    public static void setTableHeader(HSSFSheet sheet){
        HSSFRow row = sheet.createRow(0);
        //公司名称
        HSSFCell cell0 = row.createCell(0);
        cell0.setCellValue("公司名称");
        //税号
        HSSFCell cell2 = row.createCell(1);
        cell2.setCellValue("税号");
        //房间号
        HSSFCell cell3 = row.createCell(2);
        cell3.setCellValue("房间号");
        //公司地址
        HSSFCell cell4 = row.createCell(3);
        cell4.setCellValue("公司地址");
        //电话号码
        HSSFCell cell7 = row.createCell(4);
        cell7.setCellValue("电话号码");
        //开户行名称
        HSSFCell cell8 = row.createCell(5);
        cell8.setCellValue("开户行名称");
        //开户行账号
        HSSFCell cell9 = row.createCell(6);
        cell9.setCellValue("开户行账号");
        //金额
        HSSFCell cell10 = row.createCell(7);
        cell10.setCellValue("金额");
        //备注
        HSSFCell cell11 = row.createCell(8);
        cell11.setCellValue("备注");
        //填报时间
        HSSFCell cell13 = row.createCell(9);
        cell13.setCellValue("填报时间");
        //电话号码
        HSSFCell cell12 = row.createCell(10);
        cell12.setCellValue("电话号码");
        //电话号码
        HSSFCell cell14 = row.createCell(11);
        cell14.setCellValue("电话号码");
    }
    /**
     * 获取对象定义属性
     */
    public static void getManageObjectProperties(HSSFSheet sheet, List<Tax> taxes) throws IOException {
         AtomicInteger count = new AtomicInteger();
         count.getAndIncrement();
         taxes.stream().forEach(tax -> {
            HSSFRow row = sheet.createRow(count.get());
            /*公司*/
            HSSFCell cell0 = row.createCell(0);
            cell0.setCellValue(tax.getCompany());
            /*税号*/
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue( tax.getTaxNumber());
            /*房间号*/
             if(tax.getRoomNumber() != null) {
                 HSSFCell cell2 = row.createCell(2);
                 cell2.setCellValue(tax.getRoomNumber());
             }
            /*公司地址*/
            HSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(tax.getCompanyAddress());
            /*电话号码*/
            HSSFCell cell4 = row.createCell(4);
            cell4.setCellValue(tax.getTelephone());
             /*开户行名称*/
             HSSFCell cell5 = row.createCell(5);
             cell5.setCellValue(tax.getBankName());
             /*开户行账号*/
             HSSFCell cell6 = row.createCell(6);
             cell6.setCellValue(tax.getBankNumber());
             /*金额*/
             if(tax.getMoney() !=null) {
                 HSSFCell cell7 = row.createCell(7);
                 cell7.setCellValue(tax.getMoney());
             }
             /*备注*/
             HSSFCell cell8  = row.createCell(8);
             cell8.setCellValue(tax.getRemark());
             /*时间*/
             HSSFCell cell9  = row.createCell(9);
             cell9.setCellValue(tax.getDateString());
             if(tax.getPhoneNumber() !=null) {
                 /*手机号码*/
                 HSSFCell cell10 = row.createCell(10);
                 cell10.setCellValue(tax.getPhoneNumber());
             }
             /*电话号码*/
             HSSFCell cell11  = row.createCell(11);
             cell11.setCellValue(tax.getTelephone());
            count.getAndIncrement();
        });
    }
    /**
     * 调整格式
     */
    public   static  void adjustmentCell(HSSFSheet sheet, Workbook workbook){
        // 遍历所有列，调整列宽
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
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

}
