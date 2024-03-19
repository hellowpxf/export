package document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import sun.awt.geom.AreaOp;
import utils.ConstantUtils;
import utils.HttpUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


/**
 * @description:GenerateSubjectService
 * @author:pxf
 * @data:2023/03/20
 **/
@Service
public class GenerateSubjectService {
   private static HashMap hashMap =null;
    public boolean generateCore(String body, HttpServletResponse response, HttpServletRequest request) throws IOException {
        //转发接口获取数据
        String url  = "https://appcs.jbysoft.com/SXDY/business/common/matterDefinitionCombine$m=query.service";
        //获取header
        HashMap map = HttpUtils.setPostHeader(request);
        String str =  HttpUtils.doPost(url,map,body);
        System.out.println(str);
        //创建一个工作簿
        HSSFWorkbook sheets = new HSSFWorkbook();
        HSSFSheet sheet = sheets.createSheet();
        //设置表头
        setTableHeader(sheet);
        //包装数据
        getManageObjectProperties(sheet,str,map);

        //调整格式
        adjustmentCell(sheet,sheets);
        //声明输出流
        OutputStream os = null;
        //设置响应头
        setResponseHeader(response,"subject.xlsx");
        try {
            os = response.getOutputStream();
            sheets.write(os);
            sheets.close();
        } catch (Exception e) {
            e.printStackTrace();
            os.close();
            return  false;
        }
        return  true;
    }

    /**
     * 获取对象定义属性
     */
    public static void getManageObjectProperties(HSSFSheet sheet,String str,HashMap map) throws IOException {
        JSONObject responseObject = JSONObject.parseObject(str);
        //填充表数据
        System.out.println(responseObject);
        if(responseObject.getBoolean("success")) {
            JSONArray outArray = responseObject.getJSONArray("results");
            JSONArray attributes = outArray.getJSONObject(0).getJSONArray(ConstantUtils.Attribute);
            int rowNum = 1;
            for (int i = 0; i <attributes.size() ; i++) {
                JSONObject attributej = attributes.getJSONObject(i);
                JSONArray attribute = attributej.getJSONArray(ConstantUtils.Attribute);
                for (int j = 0; j <attribute.size() ; j++) {
                    JSONObject bj = attribute.getJSONObject(j);
                    JSONObject at = attribute.getJSONObject(j).getJSONObject(ConstantUtils.AttributeType);
                    HSSFRow row = sheet.createRow(rowNum);
                    int rowNUM =3;
                    //属性名称
                    HSSFCell cell3 = row.createCell(rowNUM++);
                    cell3.setCellValue(bj.getString(ConstantUtils.FieldName));
                    //属性别名
                    HSSFCell cell4 = row.createCell(rowNUM++);
                    cell4.setCellValue(bj.getString(ConstantUtils.Alias));
                    //分步名称
                    HSSFCell cell5 = row.createCell(rowNUM++);
                    cell5.setCellValue(Assert__(bj.getString(ConstantUtils.stepName)));
                    //是否显示
                    HSSFCell cell7 = row.createCell(rowNUM++);
                    cell7.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfXS)));
                    //是否编辑
                    HSSFCell cell8 = row.createCell(rowNUM++);
                    cell8.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfBJ)));
                    //是否必填
                    HSSFCell cell9 = row.createCell(rowNUM++);
                    cell9.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfBt)));
                    //是否修改算法
                    HSSFCell cell10 = row.createCell(rowNUM++);
                    cell10.setCellValue(fillAlgorith(bj,attribute,at.getInteger(ConstantUtils.SfXGSF)));
                    //默认值
                    HSSFCell cell11 = row.createCell(rowNUM++);
                    cell11.setCellValue(Assert__(at.getString(ConstantUtils.mrz)));
                    //提示语
                    HSSFCell cell12 = row.createCell(rowNUM++);
                    cell12.setCellValue(Assert__(bj.getString(ConstantUtils.tsy)));
                    //提示信息
                    HSSFCell cell122 = row.createCell(rowNUM++);
                    cell122.setCellValue(Assert__(bj.getString(ConstantUtils.tsxx)));
                    //是否作为流程参数（分支或消息提醒
                    HSSFCell cell13 = row.createCell(rowNUM++);
                    cell13.setCellValue(Assert__(at.getInteger(ConstantUtils.sfzwlcfztj)));
                    //是否作为审批规则参数
                    HSSFCell cell14 = row.createCell(rowNUM++);
                    cell14.setCellValue(Assert__(at.getInteger(ConstantUtils.isrulebody)));
                    //是否子流程参数
                    HSSFCell cell142 = row.createCell(rowNUM++);
                    cell142.setCellValue(assertISMustWriter(bj.getInteger(ConstantUtils.isChildProcess)));
                    //是否在变更清册中展示
                    HSSFCell cell17 = row.createCell(rowNUM++);
                    cell17.setCellValue(Assert__(bj.getInteger(ConstantUtils.isChangeListDisplay)));
                    //登录信息
                    HSSFCell cell18 = row.createCell(rowNUM++);
                    cell18.setCellValue(Assert__(bj.getInteger(ConstantUtils.LoginInfo),map));
                    //联想搜索显示属性
                    HSSFCell cell19 = row.createCell(rowNUM++);
                    cell19.setCellValue(Assert__(bj.getString(ConstantUtils.AssociationDisplayContent)));
                    //是否支持模糊搜索
                    HSSFCell cell6 = row.createCell(rowNUM++);
                    cell6.setCellValue(isSupportFuzzySearch(bj.getString(ConstantUtils.associatedObjectNumber),bj.getString(ConstantUtils.FieldIdentification)
                            ,bj.getString(ConstantUtils.syObjectNumber),bj.getInteger(ConstantUtils.IsFuzzySearch),bj.getInteger(ConstantUtils.caseSensitive)));
                    //联想查询类型
                    HSSFCell cell20 = row.createCell(rowNUM++);
                    cell20.setCellValue(searchType(at.getString(ConstantUtils.Type), bj.getInteger(ConstantUtils.fuzzySearchFlag)));
                    //是否加密
                    HSSFCell cell41 = row.createCell(rowNUM++);
                    cell41.setCellValue(Assert__(at.getInteger(ConstantUtils.sfjmzs)));
                    //是否加签
                    HSSFCell cell42 = row.createCell(rowNUM++);
                    cell42.setCellValue(Assert__(bj.getInteger(ConstantUtils.isAddSignature)));
                    //是否加密展示
                    HSSFCell cell43 = row.createCell(rowNUM++);
                    cell43.setCellValue(Assert__(at.getInteger(ConstantUtils.sfjmzs)));
                    //集成读卡设备
                    HSSFCell cell39 = row.createCell(rowNUM++);
                    cell39.setCellValue(Assert__(bj.getString(ConstantUtils.integratedCardReader)));
                    //读卡属性和事项拥有属性赋值关系
                    HSSFCell cell40 = row.createCell(rowNUM++);
                    cell40.setCellValue(ConstantUtils.DoubleLine);
                    //属性样式算法
                    HSSFCell cell31 = row.createCell(rowNUM++);
                    cell31.setCellValue(Assert__(bj.getString(ConstantUtils.attributeStyleName)));
                    //属性占位列数
                    HSSFCell cell32 = row.createCell(rowNUM++);
                    cell32.setCellValue(Assert__(bj.getString(ConstantUtils.attributeNumber)));
                    //属性渲染列宽
                    HSSFCell cell24 = row.createCell(rowNUM++);
                    cell24.setCellValue(Assert__(at.getString(ConstantUtils.sxxrlk)));
                    //是否作为公共属性
                    HSSFCell cell23 = row.createCell(rowNUM++);
                    cell23.setCellValue(Assert__(at.getInteger(ConstantUtils.sfzwggsx)));
                    //下拉框展示条数
                    HSSFCell cell33 = row.createCell(rowNUM++);
                    cell33.setCellValue(tagsAssert(at.getString(ConstantUtils.Type),at.getString(ConstantUtils.XLKZSTS)));
                    //管理属性隐藏时是否清空属性数据
                    HSSFCell cell34 = row.createCell(rowNUM++);
                    cell34.setCellValue(Assert__(at.getInteger(ConstantUtils.SFQKSJ)));
                    //属性规则强控时是否需退出页面
                    HSSFCell cell35 = row.createCell(rowNUM++);
                    cell35.setCellValue(Assert__(at.getInteger(ConstantUtils.SFTCYM)));
                    //是否允许录入空格
                    HSSFCell cell352 = row.createCell(rowNUM++);
                    cell352.setCellValue(isAllowBlank(at.getString(ConstantUtils.Type)));
                    //是否配置刷新实例数据
                    HSSFCell cell36 = row.createCell(rowNUM++);
                    cell36.setCellValue(Assert__(at.getInteger(ConstantUtils.SFPZSXSLSJ)));
                    //是否配置刷脸
                    HSSFCell cell362 = row.createCell(rowNUM++);
                    cell362.setCellValue(Assert__(at.getInteger(ConstantUtils.sfpzsl)));
                    //事项拥有属性和其他对象属性赋值关系
                    HSSFCell cell38 = row.createCell(rowNUM++);
                    cell38.setCellValue(ConstantUtils.DoubleLine);
                    //是否修改为互联互通
                    HSSFCell cell26 = row.createCell(rowNUM++);
                    cell26.setCellValue(Assert__(at.getInteger(ConstantUtils.sfxgwhlht)));
                    //是否配置调用条件
                    HSSFCell cell27 = row.createCell(rowNUM++);
                    cell27.setCellValue(Assert__(bj.getInteger(ConstantUtils.isConfigurationDataScope)));
                    //是否显示放大镜图标
                    HSSFCell cell28 = row.createCell(rowNUM++);
                    cell28.setCellValue(isShow(bj.getString(ConstantUtils.associatedObjectNumber),bj.getString(ConstantUtils.FieldIdentification)
                            ,bj.getString(ConstantUtils.syObjectNumber),at.getInteger(ConstantUtils.SFXSFDJTB)));
                    //是否配置时间范围
                    HSSFCell cell29 = row.createCell(rowNUM++);
                    cell29.setCellValue(Assert__(bj.getInteger(ConstantUtils.sfpzsjfw)));
                    //数据范围
                    HSSFCell cell30 = row.createCell(rowNUM++);
                    cell30.setCellValue(Assert__(bj.getString(ConstantUtils.dataRange)));
                    //流程节点设置属性
                    HSSFCell cell16 = row.createCell(rowNUM++);
                    cell16.setCellValue(setNodeMessage(at.getInteger(ConstantUtils.sfxzlcjd),
                            at.getInteger(ConstantUtils.sfzyfymxs),at.getJSONArray(ConstantUtils.LCJDSXDATE)));
                   //画像跳转
                    HSSFCell cell21 = row.createCell(rowNUM++);
                    cell21.setCellValue(isSkip(bj.getString(ConstantUtils.associatedObjectNumber),bj.getString(ConstantUtils.FieldIdentification)
                            ,bj.getString(ConstantUtils.syObjectNumber),at.getInteger(ConstantUtils.SFTZHX),at.getString(ConstantUtils.Type)));
                    //办理规则
                    HSSFCell cell22 = row.createCell(rowNUM++);
                    cell22.setCellValue(Assert__(bj.getString(ConstantUtils.ruleName)));
                    rowNum++;

                }
            }
        }
    }

    private static String isTypeBlank(String string, String string1,int s) {
        if (ConstantUtils.Input.equals(string))
        {
            if ((ConstantUtils.TextArea.equals(string1)|| ConstantUtils.Input.equals(string1)) ){
                if ( s == 1 ){
                    return  ConstantUtils.SfBT1;
                }else {
                    return  ConstantUtils.SfBT0;
                }
            }
        }
        return  ConstantUtils.DoubleLine;
    }

    private static String searchType(String dataType , int searchType){
        if ( "tags".equals(dataType)) {
            if (searchType == 0) {
                return "全查询";
            } else if (searchType == 1) {
                return "左查询";
            } else {
                return "右查询";
            }
        }else {
            return  ConstantUtils.DoubleLine;
        }
    }
    /**
     * 判断是否必填是否显示
     */
    public static  String assertISMustWriter(Integer s) {
        if (s ==null){
            return "否";
        }
        if(s==0){
            return ConstantUtils.SfBT0;
        }else if(s==1){
            return ConstantUtils.SfBT1;
        }else if(s==2){
            return ConstantUtils.SfBT2;
        }else {
            return "";
        }
    }

    /**
     * 判断是否必填是否显示
     */
    public static  String assertISMustWriter(JSONArray jsonArray) {
        if (jsonArray ==null){
            return "否";
        }
        if(jsonArray.size()<=0){
            return ConstantUtils.SfBT0;
        }else{
            return ConstantUtils.SfBT1;
        }
    }
    /** 设置浏览器下载响应头
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) {
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
     * 设置表头
     */
    private static void setTableHeader(HSSFSheet sheet){
        HSSFRow row = sheet.createRow(0);
        int rowNumT = 3;
        //属性名称
        HSSFCell cell3 = row.createCell(rowNumT++);
        cell3.setCellValue("属性名称");
        //属性别名
        HSSFCell cell4 = row.createCell(rowNumT++);
        cell4.setCellValue("属性别名");
        //分步名称
        HSSFCell cell5 = row.createCell(rowNumT++);
        cell5.setCellValue("分步名称");
        //是否显示
        HSSFCell cell7 = row.createCell(rowNumT++);
        cell7.setCellValue("是否显示");
        //是否编辑
        HSSFCell cell8 = row.createCell(rowNumT++);
        cell8.setCellValue("是否编辑");
        //是否必填
        HSSFCell cell9 = row.createCell(rowNumT++);
        cell9.setCellValue("是否必填");
        //是否修改算法
        HSSFCell cell10 = row.createCell(rowNumT++);
        cell10.setCellValue("是否修改算法");
        //默认值
        HSSFCell cell11 = row.createCell(rowNumT++);
        cell11.setCellValue("默认值");
        //提示语
        HSSFCell cell12 = row.createCell(rowNumT++);
        cell12.setCellValue("提示语");
        //提示信息
        HSSFCell cell122= row.createCell(rowNumT++);
        cell122.setCellValue("提示信息");
        //是否作为流程参数（分支或消息提醒
        HSSFCell cell13 = row.createCell(rowNumT++);
        cell13.setCellValue("是否作为流程参数（分支或消息提醒");
        //是否作为审批规则参数
        HSSFCell cell14 = row.createCell(rowNumT++);
        cell14.setCellValue("是否作为审批规则参数");
        //是否子流程参数
        HSSFCell cell15 = row.createCell(rowNumT++);
        cell15.setCellValue("是否子流程参数");
        //是否在变更清册中展示
        HSSFCell cell17 = row.createCell(rowNumT++);
        cell17.setCellValue("是否在变更清册中展示");
        //登录信息
        HSSFCell cell18 = row.createCell(rowNumT++);
        cell18.setCellValue("登录信息");
        //联想搜索显示属性
        HSSFCell cell19 = row.createCell(rowNumT++);
        cell19.setCellValue("联想搜索显示属性");
        //是否支持模糊搜索
        HSSFCell cell6 = row.createCell(rowNumT++);
        cell6.setCellValue("是否支持模糊搜索");
        //联想查询类型
        HSSFCell cell20 = row.createCell(rowNumT++);
        cell20.setCellValue("联想查询类型");
        //是否加密
        HSSFCell cell41 = row.createCell(rowNumT++);
        cell41.setCellValue("是否加密");
        //是否加签
        HSSFCell cell42 = row.createCell(rowNumT++);
        cell42.setCellValue("是否加签");
        //是否加密展示
        HSSFCell cell43 = row.createCell(rowNumT++);
        cell43.setCellValue("是否加密展示");
        //集成读卡设备
        HSSFCell cell433 = row.createCell(rowNumT++);
        cell433.setCellValue("集成读卡设备");
        //读卡属性和事项拥有属性赋值关系
        HSSFCell cell432 = row.createCell(rowNumT++);
        cell432.setCellValue("读卡属性和事项拥有属性赋值关系");
        //属性样式算法
        HSSFCell cell31 = row.createCell(rowNumT++);
        cell31.setCellValue("属性样式算法");
        //属性占位列数
        HSSFCell cell32 = row.createCell(rowNumT++);
        cell32.setCellValue("属性占位列数");
        //属性渲染列宽
        HSSFCell cell24 = row.createCell(rowNumT++);
        cell24.setCellValue("属性渲染列宽");
        //是否作为公共属性
        HSSFCell cell23 = row.createCell(rowNumT++);
        cell23.setCellValue("是否作为公共属性");
        //下拉框展示方式
        HSSFCell cell33 = row.createCell(rowNumT++);
        cell33.setCellValue("下拉框展示条数");
        //管理属性隐藏时是否清空属性数据
        HSSFCell cell231 = row.createCell(rowNumT++);
        cell231.setCellValue("管理属性隐藏时是否清空属性数据");
        //属性规则强控时是否需退出页面
        HSSFCell cell332 = row.createCell(rowNumT++);
        cell332.setCellValue("属性规则强控时是否需退出页面");
        //是否允许录入空格
        HSSFCell cell3322 = row.createCell(rowNumT++);
        cell3322.setCellValue("是否允许录入空格");
        //读卡属性和事项拥有属性赋值关系
        HSSFCell cell36 = row.createCell(rowNumT++);
        cell36.setCellValue("是否配置刷新实例数据");
        //是否配置刷脸
        HSSFCell cell363= row.createCell(rowNumT++);
        cell363.setCellValue("是否配置刷脸");
        //事项拥有属性和其他对象属性赋值关系
        HSSFCell cell34 = row.createCell(rowNumT++);
        cell34.setCellValue("事项拥有属性和其他对象属性赋值关系");
        //是否修改为互联互通
        HSSFCell cell26 = row.createCell(rowNumT++);
        cell26.setCellValue("是否修改为互联互通");
        //是否配置调用条件
        HSSFCell cell27 = row.createCell(rowNumT++);
        cell27.setCellValue("是否配置调用条件");
        //是否显示放大镜图标
        HSSFCell cell28 = row.createCell(rowNumT++);
        cell28.setCellValue("是否显示放大镜图标");
        //是否配置时间范围
        HSSFCell cell29 = row.createCell(rowNumT++);
        cell29.setCellValue("是否配置时间范围");
        //数据范围
        HSSFCell cell30 = row.createCell(rowNumT++);
        cell30.setCellValue("数据范围");
        //流程节点设置属性
        HSSFCell cell3633 = row.createCell(rowNumT++);
        cell3633.setCellValue("流程节点设置属性");
        //画像跳转
        HSSFCell cell21 = row.createCell(rowNumT++);
        cell21.setCellValue("画像跳转");
        //办理规则
        HSSFCell cell22 = row.createCell(rowNumT++);
        cell22.setCellValue("办理规则");

    }
    private  static  void adjustmentCell(HSSFSheet sheet, Workbook workbook){
        // 遍历所有列，调整列宽
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 算法填充
     * @param bj
     */
    public static String fillAlgorith(JSONObject bj,JSONArray array,Integer flag){
         String name = bj.getString(ConstantUtils.AlgorithmName);
         if(StringUtils.isBlank(name)){
             return ConstantUtils.SfBT0;
         }
         StringBuilder sb = new StringBuilder();
         Integer backExecute = bj.getInteger(ConstantUtils.IsAlgorithmBackgroundExecution);
         sb.append(name);
         sb.append(";");
         sb.append("是否后台执行：");
         sb.append(assertISMustWriter(backExecute));
        sb.append(";");
         if (flag == null) {
             return ConstantUtils.SfBT0;
         }
         if (flag == 0) {
             return ConstantUtils.SfBT0;
         }
         String attributes = bj.getString(ConstantUtils.AlgorithmAttributeSource);
         sb.append("算法触发属性：");
         sb.append(setAttribute(attributes, array));
         sb.append(";");
         return sb.toString();
    }

    public static String setAttribute(String str,JSONArray array){
        String[] split = str.split(",");
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> hmp = new HashMap<>(64);
        for (int j = 0; j < array.size() ; j++) {
            JSONObject jsonObject = array.getJSONObject(j);
            String fdf = jsonObject.getString(ConstantUtils.FieldIdentification);
            String fn = jsonObject.getString(ConstantUtils.FieldName);
            if(fdf != null){
                hmp.put(fdf,fn);
            }
        }
        for (int i = 0; i <split.length ; i++) {
            if(hmp.containsKey(split[i])){
                if(i==split.length-1){
                sb.append(hmp.get(split[i]));
                }else{
                    sb.append(hmp.get(split[i]));
                    sb.append("，");
                }

            }
        }
        return sb.toString();
    }

    /**
     * 获取登录信息数组
     * @return
     */
    private static HashMap searchLoginMessage(HashMap map) throws IOException {

        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/platformconfiguration$m=query.service";
        String body = "{\"type\":\"sxdy_dlxx\"}";
        String s = HttpUtils.doPost(url, map, body);
        System.out.println(s);
        HashMap hashMap = new HashMap(16);
        JSONObject responseObject = JSONObject.parseObject(s);
        if(responseObject.getBoolean("success")) {
            JSONArray outArray = responseObject.getJSONArray("results");
            for (int i = 0; i <outArray.size() ; i++) {
                JSONObject jsonObject = outArray.getJSONObject(i);
                String num = jsonObject.getString("num");
                String name = jsonObject.getString("name");
                if(num !=null){
                    hashMap.put(num,name);
                }
            }
            }
        return hashMap;
    }



    /**
     * 判断是否需要插入--
     * @param str
     * @return
     */
    private static String Assert__(String str){
        if(StringUtils.isBlank(str)){
            return ConstantUtils.DoubleLine;
        }else {
            return str;
        }
    }


    /**
     * 判断是否需要插入--
     * @param str
     * @return
     */
    private static String tagsAssert(String type,String str){
        if(StringUtils.isBlank(str) || !ConstantUtils.Tags.equals(type)){
            return ConstantUtils.DoubleLine;
        }else {
            return str;
        }
    }

    private static String Assert__(Integer integer,HashMap map) throws IOException {

        if (integer == null) {
            return ConstantUtils.DoubleLine;
        } else {
            if(hashMap ==null) {
                hashMap = searchLoginMessage(map);
            }
            return hashMap.get(String.valueOf(integer)).toString();
        }
    }

    /**
     * 判断是否需要插入--
     * @param str
     * @return
     */
    private static String AssertXLKZSFS__(Integer str){
        if(str == null || "".equals(str)){
            return ConstantUtils.DoubleLine;
        }else if (str==1){
            return "树状";
        }else {
            return   "非树状";
        }
    }

    /**
     * 判断是否需要插入--
     * @param str
     * @return
     */
    private static String Assert__(Integer str){
        if(str == null || "".equals(str)){
            return "否";
        }else if (str==1){
            return "是";
        }else {
            return   "否";
        }
    }

    /**
     * 模糊搜索
     * @param integer
     * @return
     */
    private static String isSupportFuzzySearch( String associatedObjectNumber,String fieldIdentification,String objecNumber, Integer isFuzzy , Integer integer) {
        String id = "dx_"+objecNumber+"_id";
        String sjdxsl = "dx_"+objecNumber+"_sjdxsl";
        if (StringUtils.isNotBlank(associatedObjectNumber) || id.equals(fieldIdentification) || sjdxsl.equals(fieldIdentification)) {
            if (isFuzzy == 1) {
                if (integer == 1) {
                    return "是；是否区分大小写搜索：是";
                } else {
                    return "是；是否区分大小写搜索：否";
                }
            } else {
                return ConstantUtils.SfBT0;
            }
        }else {
            return  ConstantUtils.SfBT0;
        }
    }

    /**
     * 是否跳转画像
     * @param
     * @return
     */
    private static String isSkip( String associatedObjectNumber,String fieldIdentification,String objecNumber, Integer isSkip ,String type) {
        String id = "dx_"+objecNumber+"_id";
        String sjdxsl = "dx_"+objecNumber+"_sjdxsl";
        if ((StringUtils.isNotBlank(associatedObjectNumber) || id.equals(fieldIdentification)
                || sjdxsl.equals(fieldIdentification) || ConstantUtils.Input.equals(type)) && isSkip != null) {
            if (isSkip == 1) {
               return  ConstantUtils.SfBT1;
            } else {
                return ConstantUtils.SfBT0;
            }
        }else {
            return  ConstantUtils.SfBT0;
        }
    }

    /**
     * 是否跳转画像
     * @param
     * @return
     */
    private static String isShow( String associatedObjectNumber,String fieldIdentification,String objecNumber, Integer isSkip) {
        String id = "dx_"+objecNumber+"_id";
        String sjdxsl = "dx_"+objecNumber+"_sjdxsl";
        if ((StringUtils.isNotBlank(associatedObjectNumber) || id.equals(fieldIdentification)
                || sjdxsl.equals(fieldIdentification) ) && isSkip != null) {
            if (isSkip == 1) {
                return  ConstantUtils.SfBT1;
            } else {
                return ConstantUtils.SfBT0;
            }
        }else {
            return  ConstantUtils.SfBT0;
        }
    }




    private static String  setNodeMessage(Integer sfxzlcjd,Integer sfzyfymxs ,JSONArray jsonArray) {
        if (sfxzlcjd == null || "".equals(sfxzlcjd) || sfxzlcjd ==0) {
            return ConstantUtils.SfBT0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("是；");
        if ( sfzyfymxs != null &&  sfzyfymxs == 1){
            sb.append("是；");
        }else {
            sb.append("否；");
        }
        for (int i = 0; i < jsonArray.size() ; i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String lcjdmc = object.getString("lcjdmc");
            sb.append(lcjdmc);
            sb.append("；");
            Integer ishow = object.getInteger("sfxs");
            Integer sfbj = object.getInteger("sfbj");
            if (ishow != null &&  ishow == 1){
                sb.append("是；");
            }else {
                sb.append("否；");
            }
            if ( sfbj != null && sfbj == 1){
                sb.append("是；");
            }else {
                sb.append("否；");
            }
        }
    return sb.toString();
    }
    public  static  String isAllowBlank(String type) {
      if( ConstantUtils.Input.equals(type)){
            return  ConstantUtils.SfBT1;
        }else {
            return  ConstantUtils.SfBT0;
        }
    }

}
