package document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import utils.ConstantUtils;
import utils.HttpUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @description:ExcelGenerate
 * @author:pxf
 * @data:2023/03/17
 **/
@Service
public class GenerateObjectService {
    private  int rowNum = 11;
    private static HashMap headerMap;
    private  static  String fileName;

    public boolean generateCore(String body, HttpServletResponse response, HttpServletRequest request) throws IOException {
        //转发接口获取数据
        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/manageObjectProperties$m=query.service";
        //设置请求头
        HashMap headermap = HttpUtils.setPostHeader(request);
        String str = HttpUtils.doPost(url, headermap, body);
        //创建一个工作簿
        HSSFWorkbook sheets = new HSSFWorkbook();
        HSSFSheet sheet = sheets.createSheet();
        //设置表头
        setTableHeader(sheet);
        //包装数据
        getManageObjectProperties(sheet, str,request);
        //调整格式
        adjustmentCell(sheet, sheets);
        //声明输出流
        OutputStream os = null;
        //设置响应头
        setResponseHeader(response, "object.xlsx");
        try {
            os = response.getOutputStream();
            sheets.write(os);
            sheets.close();
        } catch (Exception e) {
            e.printStackTrace();
            os.close();
            return false;
        }
        return true;
    }

    /**
     * 输出对象解码表
     * @param body
     * @param response
     * @param request
     * @return
     * @throws IOException
     */
    public boolean generateCore1(String body, HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = GenerateObjectService.class.getClassLoader().getResourceAsStream("object.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("对象解码模板");
        headerMap = HttpUtils.setPostHeader(request);

        //包装表头数据
        setHeaderMessage(sheet,body);
        //主体数据
        int contentRow = getManageObjectPropertiesNew(sheet,body,rowNum);

        //往下移八行
        rowNum +=  contentRow + 7;

       //事项内容拼接
        int subRow = getSubOBJContent(sheet, body, rowNum);
        rowNum = rowNum + subRow + 10;

        //指标数据
        int zbRow = getZbContent(sheet, body, rowNum);

        //对象概况
        setDescription(sheet,0,subRow,contentRow,zbRow,0);
        //声明输出流
        OutputStream os = null;
        //获取对象对象名称

        fileName =fileName+"对象解码表.xlsx";
        //设置响应头
        setResponseHeader(response, "object.xlsx");
        //合并单元格
        // mergeSheet(sheet,);
        try {
            os = response.getOutputStream();
            workbook.write(os);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            os.close();
            return false;
        }
        rowNum =11;
        return true;
    }

    /**
     * 获取对象定义属性
     */
    public static void getManageObjectProperties(HSSFSheet sheet, String str,HttpServletRequest request) throws IOException {
        JSONObject responseObject = JSONObject.parseObject(str);
        //填充表数据
        if (responseObject.getBoolean("success")) {
            JSONArray resultArray = responseObject.getJSONArray("results");
            for (int i = 1; i <= resultArray.size(); i++) {
                JSONObject detailObj = resultArray.getJSONObject(i - 1);
                HSSFRow row = sheet.createRow(i);
                //属性名称
                HSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(detailObj.getString(ConstantUtils.FieldName));

                //属性实例名称
                HSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(detailObj.getString(ConstantUtils.FieldName));

                //属性类别
                HSSFCell cell4 = row.createCell(4);
                cell4.setCellValue(detailObj.getString(ConstantUtils.DimensionClassesName));
                JSONObject attributeType = detailObj.getJSONObject(ConstantUtils.AttributeType);

                //结果属性类别
                HSSFCell cell5 = row.createCell(5);
                cell5.setCellValue(isNull(detailObj.getString(ConstantUtils.PropertiesTypeName)));

                //属性样式
                HSSFCell cell6 = row.createCell(6);
                cell6.setCellValue(GenerateObjectService.assertType(attributeType.getString(ConstantUtils.SxYS)));

                //设置数组里面的内容
                HSSFCell cell7 = row.createCell(7);
                cell7.setCellValue(GenerateObjectService.assertType1(attributeType.getString(ConstantUtils.Type)
                        , attributeType));
                //数据来源
                HSSFCell cell8 = row.createCell(8);
                cell8.setCellValue(GenerateObjectService.assertDataSource(detailObj.getInteger(ConstantUtils.DataSource)));

                //属性数据
                HSSFCell cell9 = row.createCell(9);
                cell9.setCellValue(setPropertiesValues(detailObj.getInteger(ConstantUtils.DataSource),request,detailObj));

                //是否模糊搜索
                HSSFCell cell10 = row.createCell(10);
                cell10.setCellValue(GenerateObjectService.isMustFull(detailObj.getInteger(ConstantUtils.DropDownAssociation)));

                //是否必填
                HSSFCell cell11 = row.createCell(11);
                cell11.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfBt)));

                //是否显示
                HSSFCell cell12 = row.createCell(12);
                cell12.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfXS)));

                //是否在对象项画像中显示
                HSSFCell cell13 = row.createCell(13);
                cell13.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfZHxZS)));

                //是否可编辑
                HSSFCell cell14 = row.createCell(14);
                cell14.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfBJ)));

                //算法名称
                HSSFCell cell15 = row.createCell(15);
                cell15.setCellValue("无");

                //属性描述
                HSSFCell cell16 = row.createCell(16);
                cell16.setCellValue(detailObj.getString(ConstantUtils.Description));

                //是否唯一/关键属性
                HSSFCell cell17 = row.createCell(17);
                cell17.setCellValue(GenerateObjectService.assertISMustWriter(detailObj.getInteger(ConstantUtils.keyAttribute)));

                //属性密级
                HSSFCell cell18 = row.createCell(18);
                cell18.setCellValue(isNull(detailObj.getString(ConstantUtils.AttributeClassificationName)));
            }

        }
    }


    /**
     * 设置头部信息
     */
    public  static String setHeaderMessage(XSSFSheet sheet, String body) {
        //转发接口获取数据
        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/manageObject$m=query.service";
        String str = sentPost(url, body);
        JSONObject contObj = JSONObject.parseObject(str);
        JSONArray headerArray = contObj.getJSONArray("results");
        JSONObject obj = headerArray.getJSONObject(0);
        if(headerArray != null && headerArray.size()>0){
             String fin  = obj.getString("syObjectName");
            fileName = fin;
            //对象名称
            sheet.getRow(1).getCell(1).setCellValue(fin);
            //对象类型
            String objType = obj.getString("objectDataCategory");
            String type = "";
            if("01".equals(objType)){
                type = "管理对象";
            }else if("02".equals(objType)){
                type = "服务对象";
            }else if("03".equals(objType)){
                type = "业务对象";
            }else {
                type = "--";
            }
            sheet.getRow(2).getCell(1).setCellValue(type);
        }
        //是否允许配置修改类事项
        sheet.getRow(3).getCell(1).setCellValue("是");
        //管理对象
        String objType = obj.getString("objectManager");
        String superObjectName = getObjectName(url,body,objType);
        JSONObject superStr = JSONObject.parseObject(superObjectName);
        JSONArray superObjArray = superStr.getJSONArray("results");
        JSONObject superObj = headerArray.getJSONObject(0);
        if(superObjArray != null && superObjArray.size()>0) {
            sheet.getRow(4).getCell(1).setCellValue(superObj.getString("syObjectName"));
        }
        //服务标识
        sheet.getRow(5).getCell(1).setCellValue("组织与人力资源");
        //是否内部对象
        int  isInt = obj.getIntValue("internalObject");
        sheet.getRow(6).getCell(1).setCellValue(isInt==1?"是":"否");
        //管理组织
        sheet.getRow(7).getCell(1).setCellValue("无");
        //管理角色
        sheet.getRow(8).getCell(1).setCellValue("无");
        //对象描述
        sheet.getRow(9).getCell(1).setCellValue(obj.getString("syObjectDesc"));
        return superObjectName;
    }

    /**
     * 获取对象定义属性
     */
    public static int getManageObjectPropertiesNew(XSSFSheet sheet ,String body,int rowNum) throws IOException {
        //转发接口获取数据
        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/manageObjectProperties$m=query.service";
        String str = sentPost(url, body);
        //数据转换
        JSONObject contObj = JSONObject.parseObject(str);
        JSONArray contentArray = contObj.getJSONArray("results");
        //单元格往下移
        sheet.shiftRows(rowNum, sheet.getLastRowNum(), contentArray.size() - 1);
        for (int i = rowNum; i < contentArray.size() + rowNum; i++) {
            JSONObject detailObj = contentArray.getJSONObject(i - rowNum);
            XSSFRow row = sheet.createRow(i);
            //属性名称
            XSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(detailObj.getString(ConstantUtils.FieldName));
            //属性实例名称
            XSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(detailObj.getString(ConstantUtils.FieldName));
            //属性类别
            XSSFCell cell4 = row.createCell(4);
            cell4.setCellValue(detailObj.getString(ConstantUtils.DimensionClassesName));
            JSONObject attributeType = detailObj.getJSONObject(ConstantUtils.AttributeType);
            //属性样式
            XSSFCell cell5 = row.createCell(5);
            cell5.setCellValue(GenerateObjectService.assertType(attributeType.getString(ConstantUtils.SxYS)));
            //设置数组里面的内容
            XSSFCell cell6 = row.createCell(6);
            cell6.setCellValue(GenerateObjectService.assertType1(attributeType.getString(ConstantUtils.Type)
                    , attributeType));
            //数据来源
            XSSFCell cell7 = row.createCell(7);
            cell7.setCellValue(GenerateObjectService.assertDataSource(detailObj.getInteger(ConstantUtils.DataSource)));

            //属性数据
            XSSFCell cell8 = row.createCell(8);
            cell8.setCellValue("--");

            //是否模糊搜索
            XSSFCell cell9 = row.createCell(9);
            cell9.setCellValue(GenerateObjectService.isFuzzy(attributeType.getString(ConstantUtils.SxYS)));

            //是否必填
            XSSFCell cell10 = row.createCell(10);
            cell10.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfBt)));

            //是否必填
            XSSFCell cell11 = row.createCell(11);
            cell11.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfXS)));

            //是否在对象项画像中显示
            XSSFCell cell12 = row.createCell(12);
            cell12.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfZHxZS)));

            //是否在对象项画像中显示
            XSSFCell cell13 = row.createCell(13);
            cell13.setCellValue(GenerateObjectService.assertISMustWriter(attributeType.getInteger(ConstantUtils.SfBJ)));

            //算法名称
            XSSFCell cell14 = row.createCell(14);
            cell14.setCellValue("无");

            //属性描述
            XSSFCell cell15 = row.createCell(15);
            cell15.setCellValue(detailObj.getString(ConstantUtils.Description));

            //是否唯一/关键属性
            XSSFCell cell16 = row.createCell(16);
            cell16.setCellValue(GenerateObjectService.assertISMustWriter(detailObj.getInteger(ConstantUtils.keyAttribute)));
        }
          //合并单元格
          CellRangeAddress region = new CellRangeAddress(11, 11+contentArray.size()-1, 1, 1);
          sheet.addMergedRegion(region);
          CellRangeAddress region2 = new CellRangeAddress(10, 11+contentArray.size()-1, 0, 0);
          sheet.addMergedRegionUnsafe(region2);
          return  contentArray.size();
    }

    /**
     * 判断属性样式
     */
    public static String assertType(String s) {
        if (ConstantUtils.DatePicker.equals(s)) {
            return ConstantUtils.DatePickerValue;
        } else if (ConstantUtils.Input.equals(s)) {
            return ConstantUtils.InputValue;
        } else if (ConstantUtils.RangePicker.equals(s)) {
            return ConstantUtils.RangePickerValue;
        } else if (ConstantUtils.Select.equals(s)) {
            return ConstantUtils.SelectValue;
        } else if (ConstantUtils.InputNumber.equals(s)) {
            return ConstantUtils.InputValue;
        } else {
            return "";
        }
    }

    /**
     * 判断属性样式说明
     */
    public static String assertType1(String s, JSONObject jsonObject) {
        if (ConstantUtils.Year.equals(s)) {
            return ConstantUtils.YearValue;
        } else if (ConstantUtils.Month.equals(s)) {
            return ConstantUtils.MonthValue;
        } else if (ConstantUtils.Date.equals(s)) {
            return ConstantUtils.DateValue;
        } else if (ConstantUtils.Time.equals(s)) {
            return ConstantUtils.TimeValue;
        } else if (ConstantUtils.Select.equals(s)) {
            return ConstantUtils.SelectValue;
        } else if (ConstantUtils.Project.equals(s)) {
            return ConstantUtils.ProjectValue;
        } else if (ConstantUtils.Tags.equals(s)) {
            return ConstantUtils.TagsValue;
        } else if (ConstantUtils.Multiple.equals(s)) {
            return ConstantUtils.MultipleValue;
        } else if (ConstantUtils.Input.equals(s)) {
            return "长度：" + jsonObject.getString(ConstantUtils.MaxLength) + ConstantUtils.
                    InputValue;
        } else if (ConstantUtils.TextArea.equals(s)) {
            return "长度：" + jsonObject.getString(ConstantUtils.MaxLength) + ConstantUtils.
                    TextAreaValue;
        } else if (ConstantUtils.Money.equals(s)) {
            int min = jsonObject.getString(ConstantUtils.Max) == null ? jsonObject.getInteger(ConstantUtils.Min) : 0;
            return min + "-" + jsonObject.getString(ConstantUtils.Max) + ";" +
                    ConstantUtils.MoneyValue;
        } else if (ConstantUtils.Number.equals(s)) {
            return jsonObject.getString(ConstantUtils.Min) + "-" + jsonObject.getString(ConstantUtils.Max) + ";" +
                    ConstantUtils.NumberValue;
        } else {
            return "";
        }
    }

    /**
     * 判断数据源
     */
    public static String assertDataSource(Integer s) {
        if (s == 0) {
            return ConstantUtils.SystemGenerate;
        } else if (s == 1) {
            return ConstantUtils.SelfInput;
        } else if (s == 2) {
            return ConstantUtils.FullConnection;
        } else if (s == 3) {
            return ConstantUtils.SelectValue;
        }else {
            return ConstantUtils.AssociateObject;
        }
    }

    /**
     * 判断是否必填是否显示
     */
    public static String assertISMustWriter(Integer s) {
        if (s == null) {
            return ConstantUtils.DoubleLine ;
        }
        if (s == 0) {
            return ConstantUtils.SfBT0;
        } else if (s == 1) {
            return ConstantUtils.SfBT1;
        } else if (s == 2) {
            return ConstantUtils.SfBT2;
        } else {
            return "";
        }
    }

    /**
     * 设置浏览器下载响应头
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.addHeader("Content-disposition", "attachment;filename=" + fileName + ";filename*=UTF-8" + fileName); // 内容描述
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 设置表头
     */
    private static void setTableHeader(HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(0);
        //属性名称
        HSSFCell cell2 = row.createCell(2);
        cell2.setCellValue("属性名称");
        //属性实例名称
        HSSFCell cell3 = row.createCell(3);
        cell3.setCellValue("属性实例名称");
        //属性类别
        HSSFCell cell4 = row.createCell(4);
        cell4.setCellValue("属性类别");

        //结果属性类别
        HSSFCell cell5 = row.createCell(5);
        cell5.setCellValue("结果属性类别");

        //属性样式
        HSSFCell cell6 = row.createCell(6);
        cell6.setCellValue("属性样式");

        //设置数组里面的内容
        HSSFCell cell7 = row.createCell(7);
        cell7.setCellValue("属性样式说明");

        //数据来源
        HSSFCell cell8 = row.createCell(8);
        cell8.setCellValue("属性数据来源");

        //属性数据
        HSSFCell cell9 = row.createCell(9);
        cell9.setCellValue("属性数据");

        //是否模糊搜索
        HSSFCell cell10 = row.createCell(10);
        cell10.setCellValue("是否作为实例信息模糊搜索");

        //是否必填
        HSSFCell cell11 = row.createCell(11);
        cell11.setCellValue("是否必填");

        //是否显示
        HSSFCell cell12 = row.createCell(12);
        cell12.setCellValue("是否显示");

        //是否在对象项画像中显示
        HSSFCell cell13 = row.createCell(13);
        cell13.setCellValue("是否在对象画像中显示");

        //算法名称
        HSSFCell cell14 = row.createCell(14);
        cell14.setCellValue("是否可编辑");

        //算法名称
        HSSFCell cell15 = row.createCell(15);
        cell15.setCellValue("算法名称");

        //属性描述
        HSSFCell cell16 = row.createCell(16);
        cell16.setCellValue("属性描述");

        //是否唯一/关键属性
        HSSFCell cell17 = row.createCell(17);
        cell17.setCellValue("是否唯一/关键属性");

        //属性密级
        HSSFCell cell18 = row.createCell(18);
        cell18.setCellValue("属性密级");

    }

    /**
     * 指标数据填充
     *
     * @param sheet
     * @param body
     */
    public static int getZbContent(XSSFSheet sheet, String body, int rowNum) {
        //指标内容拼接
        //转发接口获取数据
        String url3 = "https://appcs.jbysoft.com/V2/GLDX/business/common/manageObjectExtendNewVersion$m=query.service";
        JSONObject bodyParam = JSONObject.parseObject(body);
        bodyParam.put("extendClass","dxdyzb");
        String strzb = null;
        try {
            strzb = HttpUtils.doPost(url3, headerMap,bodyParam.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //数据转换
        JSONObject zbObj = JSONObject.parseObject(strzb);
        JSONArray zbArray = zbObj.getJSONArray("results");
        //指标数据
        System.out.println(rowNum);
        sheet.removeRow(sheet.getRow(rowNum));
        if (zbArray.size()>1){
            sheet.shiftRows(rowNum, sheet.getLastRowNum(), zbArray.size()-1);
        }
        for (int i = 0; i < zbArray.size(); i++) {
            XSSFRow row = sheet.createRow(rowNum+i);
            JSONObject nodeObject = zbArray.getJSONObject(i).getJSONObject("note");
            row.createCell(1).setCellValue(nodeObject.getString("zbmc"));
            row.createCell(2).setCellValue("结果");
            row.createCell(3).setCellValue((nodeObject.getIntValue("sfcwzb")) == 0 ? "否" : "是");
            row.createCell(4).setCellValue((getZBDW(nodeObject.getString("zbdw"))));
            row.createCell(5).setCellValue(nodeObject.getString("zbyxlx") == "02" ? "实例指标" : "过程指标");
            XSSFCell cell6 = row.createCell(6);
            if ( nodeObject.getInteger("sfzglsjxs") != null){
                cell6.setCellValue(nodeObject.getInteger("sfzglsjxs")==1?"是":"否");
            }
            JSONArray jArry = nodeObject.getJSONArray("shijian");
            if (jArry != null) {
                String leaveTime = jArry.getJSONObject(0).getString("label");
                row.createCell(7).setCellValue(leaveTime);
            } else {
                row.createCell(7).setCellValue("--");
            }
            JSONArray zqArray = nodeObject.getJSONArray("sjzqChecked");
            System.out.println(zqArray);
            StringBuilder sbStr = new StringBuilder(32);
            for (int j = 0; j <zqArray.size(); j++) {
                System.out.println(j);
                if ("01".equals(zqArray.get(j).toString())){
                    sbStr.append("日、");
                }else if("02".equals(zqArray.get(j).toString())) {
                    sbStr.append("周、");
                }else if("03".equals(zqArray.get(j).toString())) {
                    sbStr.append("月、");
                }else if("04".equals(zqArray.get(j).toString())) {
                    sbStr.append("季、");
                }else if("05".equals(zqArray.get(j).toString())) {
                    sbStr.append("半年、");
                }else if("06".equals(zqArray.get(j).toString())) {
                    sbStr.append("年、");
                }else if("07".equals(zqArray.get(j).toString())) {
                    sbStr.append("小时、");
                }
            }
            if (sbStr.length()>0) {
                sbStr.deleteCharAt(sbStr.length() - 1);
                row.createCell(8).setCellValue(sbStr.toString());
            }
            //空间
            JSONArray kjArray = nodeObject.getJSONArray("kongjianChecked");
            System.out.println(kjArray);
            if (kjArray != null) {
                StringBuilder kjStr = new StringBuilder(32);
                for (int j = 0; j < kjArray.size(); j++) {
                    kjStr.append(kjArray.get(j).toString());
                    kjStr.append("、");
                }
                if (kjStr.length() > 0) {
                    kjStr.deleteCharAt(kjStr.length() - 1);
                    row.createCell(9).setCellValue(kjStr.toString());
                }
            }
            //空间类型
            JSONArray kjTypeArray = nodeObject.getJSONArray("kjlxChecked");
            System.out.println(kjTypeArray);
            if (kjTypeArray != null) {
                StringBuilder kjTypeStr = new StringBuilder(32);
                for (int j = 0; j < kjTypeArray.size(); j++) {
                    System.out.println(j);
                    if ("01".equals(kjTypeArray.get(j).toString())) {
                        kjTypeStr.append("国家、");
                    } else if ("02".equals(kjTypeArray.get(j).toString())) {
                        kjTypeStr.append("区、");
                    } else if ("03".equals(kjTypeArray.get(j).toString())) {
                        kjTypeStr.append("市、");
                    } else if ("04".equals(kjTypeArray.get(j).toString())) {
                        kjTypeStr.append("省、");
                    }
                }
                if (kjTypeStr.length() > 0) {
                    kjTypeStr.deleteCharAt(kjTypeStr.length() - 1);
                    row.createCell(10).setCellValue(kjTypeStr.toString());
                }
            }
         //类别
            JSONArray typeArray = nodeObject.getJSONArray("leibie");
            System.out.println(typeArray);
            if (typeArray != null && typeArray.size()>0) {
                StringBuilder typeStr = new StringBuilder(32);
                for (int j = 0; j < typeArray.size(); j++) {
                    JSONObject attrObj = typeArray.getJSONObject(j);
                    if (attrObj != null){
                        typeStr.append(attrObj.getString("label"));
                        typeStr.append("、");
                    }
                }
                if (typeStr.length() > 0) {
                    typeStr.deleteCharAt(typeStr.length() - 1);
                    row.createCell(11).setCellValue(typeStr.toString());
                }
            }
            //统计类型
            String countTypeArr = nodeObject.getString("tjlx");
            String result = "";
            if ("01".equals(countTypeArr)){
                result = "发生数";
            }else if("02".equals(countTypeArr)){
                result =  "年累计";
            }else if("03".equals(countTypeArr)){
                result = "历史累计";
            }else if("04".equals(countTypeArr)){
                result = "季累计";
            }else if("05".equals(countTypeArr)){
                result = "月累计";
            }else if("06".equals(countTypeArr)){
                result = "运算类";
            }else {
                result = "--";
            }
            row.createCell(12).setCellValue(result);

            //图表
            JSONArray tbCheckd = nodeObject.getJSONArray("tubiao");
            if (tbCheckd != null && tbCheckd.size()>0){
                StringBuffer nameBuffer = new StringBuffer(128);
                StringBuffer isMap = new StringBuffer(4);
                StringBuffer decBuffer = new StringBuffer(128);
                for (int j = 0; j < tbCheckd.size() ; j++) {
                    JSONObject obj = tbCheckd.getJSONObject(j);
                    if (obj != null){
                        nameBuffer.append(obj.getString("label"));
                        nameBuffer.append("、");
                        String  mp = obj.getString("sfdtzstb")=="0"?"是":"否";
                        isMap.append(mp);
                        isMap.append("、");
                        decBuffer.append(obj.getString("value"));
                        decBuffer.append("、");
                    }
                }
                if (nameBuffer.length()>0 && isMap.length()>0 && decBuffer.length()>0){
                    nameBuffer.deleteCharAt(nameBuffer.length() - 1);
                    row.createCell(13).setCellValue(nameBuffer.toString());
                    isMap.deleteCharAt(isMap.length() - 1);
                    row.createCell(14).setCellValue(isMap.toString());
                    decBuffer.deleteCharAt(decBuffer.length() - 1);
                    row.createCell(15).setCellValue(nodeObject.getString("zbms"));
                }
            }
        }
        return zbArray.size();
    }

    /**
     * 事项定义数据填充
     *
     * @param sheet
     * @param body
     */
    public static int getSubOBJContent(XSSFSheet sheet, String body, int flag) {
        //事项
        //转发接口获取数据
        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/matterSubject$m=query.service";
        String subObjectContent = sentPost(url,body);
        //数据转换
        JSONObject subObj = JSONObject.parseObject(subObjectContent);
        JSONArray subArry = subObj.getJSONArray("results");
        //删除一行
        sheet.removeRow(sheet.getRow(flag));
        if (subArry.size()>1){
            sheet.shiftRows(flag, sheet.getLastRowNum(), subArry.size()-1);
        }
        for (int i = 0; i < subArry.size(); i++) {
            JSONObject obj = subArry.getJSONObject(i);
            XSSFRow row = sheet.createRow(flag+i);
            //事项名称
            row.createCell(1).setCellValue(obj.getString("matterSubjectName"));
            //事项类型
            String uClassify = obj.getString("userClassify");
            if ("1".equals(uClassify)){
                row.createCell(2).setCellValue("修改");
            }else if("0".equals(uClassify)){
                row.createCell(2).setCellValue("新增");
            }else if ("3".equals(uClassify)){
                row.createCell(2).setCellValue("注销");
            }else {
                row.createCell(2).setCellValue("--");
            }
            //标准时长
            row.createCell(3).setCellValue("0");
            row.createCell(4).setCellValue("无");
            //是否子事项
            row.createCell(5).setCellValue(obj.getInteger("subItem") == 1 ? "是" : "否");
            //事项描述
            row.createCell(6).setCellValue(obj.getString("matterSubjectDescription"));
        }
        return  subArry.size();
    }


    private static void adjustmentCell(HSSFSheet sheet, Workbook workbook) {
        // 遍历所有列，调整列宽
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
        Iterator<Row> rowIterator = sheet.rowIterator();
    }

    private static void mergeSheet(XSSFSheet sheet) {
        // 遍历所有列，调整列宽
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

    }

    /**
     * 判断是否模糊搜索
     *
     * @param s
     */
    private static String isFuzzy(String s) {
        if (s == null) {
            return ConstantUtils.DoubleLine;
        }
        if (ConstantUtils.Input.equals( s)) {
            return ConstantUtils.SfBT0;
        } else {
            return ConstantUtils.DoubleLine;
        }
    }

    /**
     * 指标单位查询
     */
    private static String getZBDW(String num) {
        //转发接口获取数据
        JSONObject body = new JSONObject(2);
        body.put("type", "zbdy");
        body.put("num", num);
        String url2 = "https://appcs.jbysoft.com/V3/SJZT/business/zb/platformconfiguration$m=query.service";
        String strzb = null;
        System.out.println(url2 + "------------body" + body);
        try {
            strzb = HttpUtils.doPost(url2, headerMap, body.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strzb);
        JSONArray jsonArray = JSONObject.parseObject(strzb).getJSONArray("results");
        if (jsonArray != null && jsonArray.size()>0){
            JSONObject object = jsonArray.getJSONObject(0);
            if(object != null){
                return  object.getString("name");
            }else {
                return  "--";
            }
        }else {
            return  "--";
        }
    }
    /**
     * 发送请求
     */
    public static  String sentPost(String url,String body){
        try {
            String result = HttpUtils.doPost(url, headerMap, body);
            System.out.println(result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 查对象名字
     */
    public static String getObjectName(String url,String body, String objType){
        if (objType != null){
            String[] split = objType.split("/");
            String objNum = split[(split.length-1)];
            JSONObject bodyType = JSONObject.parseObject(body);
            bodyType.put("syObjectNumber",objNum);
           return sentPost(url,bodyType.toString());
        }
      return "--";
    }
    /**
     * 设置描述
     */
    public static void setDescription(XSSFSheet sheet,int subjectType,int subject,int attribute,int target,int report){
        String cell = sheet.getRow(9).getCell(1).getStringCellValue();
        String value = cell+"。"+"包含"+subjectType+"个项目分类，"+subject+"个事项，"+attribute+"个属性，"+target+"个管理指标，"+report+"个报表";
        sheet.getRow(9).getCell(1).setCellValue(value);
    }


    private static String isMustFull(Integer integer) {
        if (integer ==0){
            return "否";
        }else if (integer == 1){
            return "是";
        }else {
            return "--";
        }
    }

    private static String isNull(String str) {
        if (str == null || "".equals(str)){
            return "--";
        }else {
           return  str;
        }
    }

    public static String setPropertiesValues( Integer type , HttpServletRequest request ,JSONObject obj){
        if(type == 3){
            //转发接口获取数据
            String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/objectAttributeOptionScope$m=query.service";
            //设置请求头
            HashMap headermap = HttpUtils.setPostHeader(request);
            String str = null;
            StringBuilder sb = new StringBuilder();
            JSONObject body = new JSONObject();
            body.put("fieldIdentification",obj.getString("fieldIdentification"));
            body.put("organizationNumber",obj.getString("organizationNumber"));
            body.put("syObjectNumber",obj.getString("syObjectNumber"));
            try {
                str = HttpUtils.doPost(url, headermap, body.toJSONString());
                if (StringUtils.isNotBlank(str
                )){
                    JSONObject jsonObject = JSONObject.parseObject(str);
                    if (jsonObject != null && jsonObject.size()>0){
                        JSONArray result = jsonObject.getJSONArray("results");
                        if (result != null && result.size()>0) {
                            for (Object array : result
                            ) {
                                JSONObject j = (JSONObject) array;
                                sb.append(j.getString("name"));
                                sb.append("、");
                            }
                            return  sb.toString();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  ConstantUtils.DoubleLine;
        }else if (type == 5 ){

            //转发接口获取数据
            String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/manageObject$m=query.service";
            //设置请求头
            HashMap headermap = HttpUtils.setPostHeader(request);
            //设置请体
            JSONObject body = new JSONObject();
            body.put("organizationNumber",obj.getString("organizationNumber"));
            body.put("syObjectNumber",obj.getString("associatedObjectNumber"));
            String str = null;
            StringBuilder sb = new StringBuilder();

            try {
                str = HttpUtils.doPost(url, headermap, body.toJSONString());
                if (StringUtils.isNotBlank(str
                )){
                    JSONObject jsonObject = JSONObject.parseObject(str);
                    if (jsonObject != null && jsonObject.size()>0){
                        JSONArray result = jsonObject.getJSONArray("results");
                        if (result != null && result.size()>0) {
                            for (Object array : result
                            ) {
                                JSONObject j = (JSONObject) array;
                                sb.append(j.getString("syObjectName"));
                            }
                            return  sb.toString();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ConstantUtils.DoubleLine;
        }else {
            return  ConstantUtils.DoubleLine;
        }

    }

}
