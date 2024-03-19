package document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import utils.ConstantUtils;
import utils.HttpUtils;

import javax.annotation.processing.RoundEnvironment;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @description:GenerateSubjectFile
 * @author:pxf
 * @data:2023/07/12
 **/
@Service
public class GenerateSubjectFileService {
    private static HashMap hashMap = null;
    private static int rowNum = 6;
    private static String fileName;

    public boolean generateCore(String body, HttpServletResponse response, HttpServletRequest request) throws IOException {
        hashMap = HttpUtils.setPostHeader(request);
        InputStream inputStream = GenerateObjectService.class.getClassLoader().getResourceAsStream("subjectV8.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("事项解码表模板");
        //填充主体数据
        JSONArray jsonArray = getManageObjectProperties(sheet, body);
        System.out.println(jsonArray);
        //填充表头数据
        getHeaderContent(sheet, jsonArray);
        //填充关联清册
        getAssociationList(sheet, jsonArray);
        //填充影响属性
        getActiveAtrribute(sheet, jsonArray, body);
        //填充材料
        getAttachment(sheet, jsonArray);
        //填充摘要
        getBusinessSummary(sheet, jsonArray);
        //规则填充
        getRules(sheet, jsonArray);
        //指标数据填充
         getIndicator(sheet,body);
        //重置num
        rowNum = 6;
        //调整格式
        //adjustmentCell(sheet,sheets);
        //声明输出流
        OutputStream os = null;
        //设置响应头
        setResponseHeader(response, "subject.xlsx");
        try {
            os = response.getOutputStream();
            workbook.write(os);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            os.close();
            return false;
        }
        return true;
    }
    public static void getIndicator(XSSFSheet sheet,String body){
        rowNum += 5;
        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/manageObjectExtend$m=query.service";
        JSONObject bodyObj = JSONObject.parseObject(body);
        bodyObj.put("extendClass","zhibiao");
        bodyObj.put("syObjectNumber",bodyObj.getString("managementServiceObjectNumber"));
        String resultStr = sentPost(url, bodyObj.toJSONString());
        JSONArray indicators = JSONObject.parseObject(resultStr).getJSONArray("results");
        if (indicators != null && indicators.size()>0){
            sheet.shiftRows(rowNum, sheet.getLastRowNum(), indicators.size());
            for (int i = 0; i < indicators.size() ; i++) {
                JSONObject indicator = indicators.getJSONObject(i);
                XSSFRow row = sheet.createRow(rowNum);
                //指标名称
                String zbmc = indicator.getString("zbmc");
                row.createCell(3).setCellValue(Assert__(zbmc));
            }
        }

    }
    /**
     * 规则填充
     * @param sheet
     * @param outArray
     */
    public static  void getRules(XSSFSheet sheet, JSONArray outArray) {
        rowNum = rowNum+2;
        if (outArray != null && outArray.size() > 0) {
            JSONArray rules = outArray.getJSONObject(0).getJSONArray("rules");
            if (rules != null && rules.size()>0){
                sheet.shiftRows(rowNum, sheet.getLastRowNum(), rules.size());
                for (int i = 0; i < rules.size() ; i++) {
                    XSSFRow row = sheet.createRow(rowNum);
                    JSONObject ruleObj = rules.getJSONObject(i);
                    //是否根据属性区分规则
                    int disByAttr = ruleObj.getIntValue("isDistinguishRuleByAttribute");
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 2));
                    row.createCell(1).setCellValue(disByAttr == 1?"是":"否");
                    //规则名称
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 5));
                    row.createCell(3).setCellValue(ruleObj.getString("ruleName"));
                    rowNum++;
                }   
            }
        }
    }
    /**
     * 填充表头内容
     * @param sheet
     * @param outArray
     */
    public static  void getHeaderContent(XSSFSheet sheet, JSONArray outArray) {
        int headerRowNum = 0;
        XSSFRow row = null;
        if (outArray != null && outArray.size() > 0) {
            JSONObject object =  outArray.getJSONObject(0);
            //对象事项内容
            String managementServiceObjectName = object.getString("managementServiceObjectName");
            String matterSubjectName = object.getString("matterSubjectName");
            row  = sheet.getRow(headerRowNum);
            String fin = managementServiceObjectName+"对象"+matterSubjectName;
            fileName = fin;
            row.getCell(0).setCellValue(fin+"事项解码表_V8.0（版本号）");
            headerRowNum++;
            row = sheet.getRow(headerRowNum);
            //渲染方式
            String formLoadWay = object.getIntValue("formLoadWay") == 0 ? "组件渲染" : "非组件渲染";
            row.getCell(2).setCellValue(Assert__(formLoadWay));
            //终端类型
            String terminalType = object.getString("terminalType");
            String[] terminalTypes = terminalType.split(",");
            StringBuffer sb = new StringBuffer(16);
            if (terminalTypes != null && terminalType.length()>0){
                for (int i = 0; i <terminalTypes.length ; i++) {
                    if ("0".equals(terminalTypes[i])){
                        sb.append("桌面端");
                        sb.append("，");
                    }else if("1".equals(terminalTypes[i])){
                        sb.append("移动端");
                        sb.append("，");
                    }else if("2".equals(terminalTypes[i])){
                        sb.append("单位网厅");
                        sb.append("，");
                    }else if("3".equals(terminalTypes[i])){
                        sb.append("个人网厅");
                        sb.append("，");
                    }else if("4".equals(terminalTypes[i])){
                        sb.append("微信小程序");
                        sb.append("，");
                    }else if("5".equals(terminalTypes[i])){
                        sb.append("支付宝小程序");
                        sb.append("，");
                    }else if("6".equals(terminalTypes[i])){
                        sb.append("微信公众号");
                        sb.append("，");
                    }else if("7".equals(terminalTypes[i])){
                        sb.append("手机公积金");
                        sb.append("，");
                    }else if("8".equals(terminalTypes[i])){
                        sb.append("自助终端");
                        sb.append("，");
                    }else if("9".equals(terminalTypes[i])){
                        sb.append("开发商网厅");
                        sb.append("，");
                    }else if("10".equals(terminalTypes[i])){
                        sb.append("担保公司网厅");
                        sb.append("，");
                    }
                }
                sb.deleteCharAt(sb.length()-1);
            }
            row.getCell(4).setCellValue(Assert__(sb.toString()));
           //事项状态
            String isAbled = object.getIntValue("isAbled") == 1 ? "是" : "否";
            row.getCell(6).setCellValue(Assert__(isAbled));
            //是否显示
            String isApprovalRevocation = object.getIntValue("isApprovalRevocation") == 1 ? "是" : "否";
            row.getCell(8).setCellValue(Assert__(isApprovalRevocation));
            //事项是否可撤销/撤回
            String isBackgroundAutomatic = object.getIntValue("isBackgroundAutomatic") == 1 ? "是" : "否";
            row.getCell(10).setCellValue(Assert__(isBackgroundAutomatic));
            //是否审批撤销
            String isApprovalWithdrawal = object.getIntValue("isApprovalWithdrawal") == 1 ? "是" : "否";
            row.getCell(12).setCellValue(Assert__(isApprovalWithdrawal));
            //审批页面是否配置撤销功能
            String isConfigureRevocation = object.getIntValue("isConfigureRevocation") == 1 ? "是" : "否";
            row.getCell(14).setCellValue(Assert__(isConfigureRevocation));
            //撤销时审批意见是否必输
            String isRevocationOpinion = object.getIntValue("isRevocationOpinion") == 1 ? "是" : "否";
            row.getCell(16).setCellValue(Assert__(isRevocationOpinion));
            //是否展示变更清册
            String isShowChangeInfo = object.getIntValue("isShowChangeInfo") == 1 ? "是" : "否";
            row.getCell(18).setCellValue(Assert__(isShowChangeInfo));
            //是否支持暂存
            String isTemporaryStorage = object.getIntValue("isTemporaryStorage") == 1 ? "是" : "否";
            row.getCell(20).setCellValue(Assert__(isTemporaryStorage));
            //是否分屏
            String isSplitScreen = object.getIntValue("isSplitScreen") == 1 ? "是" : "否";
            row.getCell(22).setCellValue(Assert__(isSplitScreen));
            //底部按钮是否需固定
            String isBottomButtonFixed = object.getIntValue("isBottomButtonFixed") == 1 ? "是" : "否";
            row.createCell(24).setCellValue(Assert__(isBottomButtonFixed));
            //是否上区块链
            String isUpBlockchain = object.getIntValue("isUpBlockchain") == 1 ? "是" : "否";
            row.getCell(26).setCellValue(Assert__(isUpBlockchain));
            //是否支持查看完整流程
            String isViewCompleteProcess = object.getIntValue("isViewCompleteProcess") == 1 ? "是" : "否";
            row.getCell(28).setCellValue(Assert__(isViewCompleteProcess));
            //审批意见卡片是否显示
            String approvalCommentIsDisplayed = object.getIntValue("approvalCommentIsDisplayed") == 1 ? "是" : "否";
            row.getCell(30).setCellValue(Assert__(approvalCommentIsDisplayed));
            //是否多次录入
            String isMultipleEnter = object.getIntValue("isMultipleEnter") == 1 ? "是" : "否";
            row.getCell(32).setCellValue(isMultipleEnter);
            //是否配置重置功能
            String isConfReset = object.getIntValue("isConfReset") == 1 ? "是" : "否";
            row.getCell(34).setCellValue(Assert__(isConfReset));
            //打印按钮算法名称
            String printButtonAlgName = object.getString("printButtonAlgName");
            row.getCell(36).setCellValue(Assert__(printButtonAlgName));
            //打印按钮名称算法触发属性
            String printButtonAlgTrigAttr = object.getString("printButtonAlgTrigAttr");
            row.getCell(38).setCellValue(Assert__(printButtonAlgTrigAttr));
            //右侧是否显示导航栏
            String isShowNavigation = object.getIntValue("isShowNavigation")== 1 ? "是" : "否";
            row.getCell(40).setCellValue(Assert__(isShowNavigation));
            //是否在事项画像中显示
            row.getCell(42).setCellValue("暂未填充");
            
            headerRowNum++;
            row = sheet.getRow(headerRowNum);
            // //审批意见卡片是否显示
            String isAttachmentDispensabled = object.getIntValue("isAttachmentDispensabled") == 1 ? "是" : "否";
            row.getCell(2).setCellValue(Assert__(isAttachmentDispensabled));
            //容缺材料名称存储属性
            String materialNameStorageAttr = object.getString("materialNameStorageAttr");
            row.getCell(4).setCellValue(Assert__(materialNameStorageAttr));
            //规则样式
            int ruleStyle = object.getIntValue("ruleStyle");
            String ruleStyleName = "";
            if (ruleStyle ==0){
                ruleStyleName = "展开";
            }else if (ruleStyle == 1){
                ruleStyleName = "折叠";
            }else if (ruleStyle ==3 ) {
                ruleStyleName = "不显示";
            }else {
                ruleStyleName = Assert__(ruleStyleName);
            }
            row.getCell(6).setCellValue(ruleStyleName);
            //唯一业务属性
            row.getCell(8).setCellValue( Assert__(object.getString("uniqueBusinessAttribute")));
            //业务附件展示方式
            row.getCell(10).setCellValue("暂未填充");
            //页面样式
            row.getCell(12).setCellValue("暂未填充");
            //是否导入
            String isImport = object.getIntValue("isImport") == 1?"是":"否";
            row.getCell(14).setCellValue(Assert__(isImport));
            //升序属性/降序属性
            row.getCell(16).setCellValue(Assert__(object.getString("descendingAttribute")));
            //合计属性
            row.getCell(18).setCellValue(Assert__(object.getString("summationAttribute")));
            //操作列样式算法
            row.getCell(20).setCellValue("暂未填充");
            //是否全部删除
            String isDeleteAll = object.getIntValue("isDeleteAll") == 1 ? "是" : "否";
            row.getCell(22).setCellValue(Assert__(isDeleteAll));
            //是否批处理
            String isBatchProcess = object.getIntValue("isBatchProcess") == 1 ? "是" : "否";
            row.getCell(24).setCellValue(Assert__(isBatchProcess));
            //清册展示样式
            String listSh = object.getString("listSh");
            row.getCell(26).setCellValue(Assert__(listSh));
            //流程节点是否显示角色
            String isShowProcessNodeRoles = object.getIntValue("isShowProcessNodeRoles")==1?"是":"否";
            row.getCell(28).setCellValue(Assert__(isShowProcessNodeRoles));
            //帮助中心地址别名
            String helpCenterAlias = object.getString("helpCenterAlias");
            row.getCell(30).setCellValue(Assert__(helpCenterAlias));
            //是否自动关闭提示信息
            String isAutoClosePrompt = object.getIntValue("isAutoClosePrompt") == 1 ? "是" : "否";
            row.getCell(32).setCellValue(Assert__(isAutoClosePrompt));
            //是否上国密
            String isStateSecret = object.getIntValue("isStateSecret") == 1 ? "是" : "否";
            row.getCell(34).setCellValue(Assert__(isStateSecret));
            //退回时审批意见最少输入字段
            row.getCell(36).setCellValue("暂未填充");
            //是否复用签字功能
            row.getCell(38).setCellValue("暂未填充");

        }
    }

    /**
     * 摘要填充
     * @param sheet
     * @param outArray
     */
    public static  void getBusinessSummary(XSSFSheet sheet, JSONArray outArray) {
        rowNum = rowNum+4;
        if (outArray != null && outArray.size() > 0) {
            String businessSummary = outArray.getJSONObject(0).getString("businessSummary");
            XSSFRow row = sheet.getRow(rowNum);
            row.getCell(7).setCellValue(businessSummary);
            rowNum ++;
        }
    }
    /**
     * 填充材料
     *
     * @param sheet
     * @param outArray
     */
    public static void getAttachment(XSSFSheet sheet, JSONArray outArray) {
        rowNum++;
        rowNum++;
        if (outArray != null && outArray.size() > 0) {
            JSONArray attributes = outArray.getJSONObject(0).getJSONArray("attachment");
            for (int i = 0; i < attributes.size(); i++) {
                JSONObject attributej = attributes.getJSONObject(i);
                JSONArray attachments = attributej.getJSONArray("attachments");
                sheet.shiftRows(rowNum, sheet.getLastRowNum(), attachments.size());
                int cellIndex = 2;
                for (int j = 0; j < attachments.size(); j++) {
                    JSONObject jsonAtt = attachments.getJSONObject(j);
                    JSONObject attachmentDefine = jsonAtt.getJSONObject("attachmentDefine");
                    XSSFRow row = null;
                    if (attachments.size()==1){
                        row = sheet.createRow(rowNum);
                    }else {
                        row = sheet.createRow(rowNum);
                    }
                    //证明材料名称
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getString("attachmentName"));
                    //证明材料设置
                    row.createCell(cellIndex++).setCellValue("暂未填充");
                    //是否对象实例附件
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getIntValue("isObjectInstanceAttachment")==1?"是":"否");
                    //是否复用对象实例附件
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getIntValue("isCoverObjectInstanceAttachment")==1?"是":"否");
                    //附件复用算法
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getString("attachmentAlgorithmName"));
                    //附件复用算法触发属性
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getString("algorithmTriggerAttribute"));
                    //业务附件类型
                    int attachmentType = jsonAtt.getIntValue("attachmentType");
                    if (attachmentType==1){
                        row.createCell(cellIndex++).setCellValue("容缺");
                    }else if (attachmentType ==2){
                        row.createCell(cellIndex++).setCellValue("必扫");
                    }else if (attachmentType==3){
                        row.createCell(cellIndex++).setCellValue("非必扫");
                    }else {
                        row.createCell(cellIndex++).setCellValue("暂未填充");
                    }
                    //附件类型算法
                    row.createCell(cellIndex++).setCellValue("暂未填充");
                    //附件类型算法触发属性
                    row.createCell(cellIndex++).setCellValue("暂未填充" );
                    //附件可执行操作
                    row.createCell(cellIndex++).setCellValue("暂未填充" );
                    //附件操作算法
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getString("attachmentOperateAlgorithmName"));
                    //是否覆盖对象实例附件
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getIntValue("isCoverObjectInstanceAttachment")==1?"是":"否");
                    //证明材料上传说明
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getString("attachmentDescription"));
                    //是否为面签内容
                    row.createCell(cellIndex++).setCellValue(attachmentDefine.getString("attachmentDescription"));
                    //入参
                    row.createCell(cellIndex++).setCellValue("暂未填充" );
                    //出参
                    row.createCell(cellIndex++).setCellValue("暂未填充" );
                    //是否作为公共附件
                    row.createCell(cellIndex++).setCellValue(jsonAtt.getIntValue("isPublicAttachment")==0?"否":"是");
                    rowNum++;
                }
            }
        }
    }

    /**
     * 获取影响属性数据
     *notice 看不懂，我也看不懂
     */
    public static void getActiveAtrribute(XSSFSheet sheet, JSONArray outArray, String body) throws IOException {
        rowNum++;
        int flag = 0;
        if (outArray != null && outArray.size() > 0) {
            JSONArray attributes = outArray.getJSONObject(0).getJSONArray(ConstantUtils.Attribute);
            for (int i = 0; i < attributes.size(); i++) {
                JSONObject attributej = attributes.getJSONObject(i);
                JSONArray attribute = attributej.getJSONArray(ConstantUtils.Attribute);
                for (int j = 0; j < attribute.size(); j++) {
                    JSONObject bj = attribute.getJSONObject(j);
                    JSONArray kzzdList = bj.getJSONArray("kzzdList");
                    XSSFRow row = null;
                    if (kzzdList != null && kzzdList.size() > 0) {
                        sheet.shiftRows(rowNum, sheet.getLastRowNum(), kzzdList.size());
                        String fieldIdentification = bj.getString("fieldIdentification");
                        HashMap<String, Object> listHMap = getListNumberNew(kzzdList, fieldIdentification, body);
                        ArrayList arrays = null;
                        int num = 0;
                        if (listHMap != null && listHMap.size() > 0) {
                            arrays = (ArrayList) listHMap.get("con");
                            num = (int) listHMap.get("num");
                            num = kzzdList.size() / num;
                        }
                        for (int k = 0; k < arrays.size(); k++) {
                            if (arrays.size() > 0) {
                                if (flag == 0) {
                                    row = sheet.createRow(rowNum);
                                } else {
                                    row = sheet.createRow(rowNum);
                                }
                                flag++;
                                int beginCell = 6;
                                System.out.println("============rown" + rowNum);
                                JSONObject beforeObj = (JSONObject) arrays.get(k);
                                if (k == 0 ) {
                                    row.createCell(4);
                                    row.getCell(4).setCellValue(beforeObj.getString("name"));
                                }else if(((k) % num) == 0){
                                    row.createCell(4);
                                    row.getCell(4).setCellValue(beforeObj.getString("name"));
                                }
                                row.createCell(beginCell);
                                row.createCell(beginCell + 1);
                                CellRangeAddress region2 = new CellRangeAddress(row.getRowNum(), row.getRowNum(), beginCell, beginCell + 1);
                                sheet.addMergedRegionUnsafe(region2);
                                row.getCell(beginCell).setCellValue(beforeObj.getString("fieldName"));
                                JSONObject attributeTypeBefore = beforeObj.getJSONObject("attributeType");
                                setBasicParam(sheet, row, attributeTypeBefore, beginCell + 2);
                                rowNum++;
                            }
                        }
                        //设置属性别名
                        row.createCell(3);
                        row.getCell(3).setCellValue(bj.getString("fieldName"));
                    }
                }
            }
        }
    }

    /**
     * 获取关联清册数据
     */
    public static void getAssociationList(XSSFSheet sheet, JSONArray outArray) throws IOException {
        rowNum++;
        rowNum++;
        if (outArray != null && outArray.size() > 0) {
            JSONArray attributes = outArray.getJSONObject(0).getJSONArray(ConstantUtils.Attribute);
            int flag = 0;
            for (int i = 0; i < attributes.size(); i++) {
                JSONObject attributej = attributes.getJSONObject(i);
                JSONArray attribute = attributej.getJSONArray(ConstantUtils.Attribute);
                System.out.println(attribute.size());
                System.out.println(rowNum);
                for (int j = 0; j < attribute.size(); j++) {
                    JSONObject bj = attribute.getJSONObject(j);
                    JSONObject at = bj.getJSONObject("associationList");
                    XSSFRow row = null;
                    if (at != null) {
                        if (flag > 0) {
                            sheet.shiftRows(rowNum, sheet.getLastRowNum(), 1);
                            row = sheet.createRow(rowNum);
                        } else {
                            row = sheet.getRow(rowNum);
                        }
                        flag++;
                        //清册类型
                        row.createCell(2).setCellValue(at.getIntValue("listType") == 2 ? "事项" : "查询");
                        //对象名称
                        row.createCell(3).setCellValue(at.getString("listName"));
                        //查询名称
                        row.createCell(4).setCellValue("尚未填充");
                        //是否统一配置
                        row.createCell(5).setCellValue(at.getIntValue("isUnifiedConfiguration") == 1 ? "是" : "否");
                        //是否配置导入功能
                        row.createCell(6).setCellValue(at.getIntValue("isConfigureImport") == 0 ? "否" : "是");
                        JSONArray dataRange = bj.getJSONObject("dataRange").getJSONArray("qczssxData");
                        StringBuffer strBuffer = new StringBuffer(128);
                        for (int k = 0; k < dataRange.size(); k++) {
                            String name = dataRange.getJSONObject(k).getString("fieldName");
                            strBuffer.append(name);
                            strBuffer.append(",");
                        }
                        strBuffer.deleteCharAt(strBuffer.length() - 1);
                        //清册展示属性
                        row.createCell(7).setCellValue(strBuffer.toString());
                        //事项别名
                        row.createCell(8).setCellValue("尚未填充");
                        //是否显示
                        JSONObject attrType = bj.getJSONObject("attributeType");
                        row.createCell(9).setCellValue(attrType.getIntValue("sfxs") == 0 ? "否" : "是");
                        //是否可编辑
                        row.createCell(10).setCellValue(attrType.getIntValue("sfbj") == 0 ? "否" : "是");
                        //是否必输
                        row.createCell(11).setCellValue(attrType.getIntValue("sfbs") == 0 ? "否" : "是");
                        //清册加载算法
                        JSONObject listBasicInfo = at.getJSONObject("listBasicInfo");
                        row.createCell(12).setCellValue(listBasicInfo.getString("loadingAlgorithmName"));
                        //算法触发属性
                        row.createCell(13).setCellValue("尚未填充");
                        //事项清册排序
                        JSONArray listDisplayWay = at.getJSONArray("listDisplayWay");
                        if (listBasicInfo != null && listDisplayWay.size() > 0) {
                            row.createCell(14).setCellValue("尚未填充");
                        } else {
                            row.createCell(14).setCellValue("——");
                        }

                        //关联清册展示样式
                        row.createCell(15).setCellValue(listBasicInfo.getIntValue("showStyle") == 1 ? "卡片" : "清册");
                        //保存后是否需关闭页面
                        row.createCell(16).setCellValue(listBasicInfo.getIntValue("isClosePage") == 1 ? "是" : "否");
                        //关联清册数据修改时是否更新属性数据
                        row.createCell(17).setCellValue(listBasicInfo.getIntValue("isUpdateAttrData") == 1 ? "是" : "否");
                        //子事项属性
                        JSONArray listContent = at.getJSONArray("listContent");
                        if (listContent != null && listContent.size() > 0) {
                            StringBuffer subBuffer = new StringBuffer(128);
                            StringBuffer supBuffer = new StringBuffer(128);
                            for (int k = 0; k < listContent.size(); k++) {
                                String subStr = listContent.getJSONObject(k).getString("inventoryAttributeKey");
                                subBuffer.append(subStr);
                                String supStr = listContent.getJSONObject(k).getString("capabilityAttributeKey");
                                supBuffer.append(supStr);
                                subBuffer.append(",");
                                supBuffer.append(",");
                            }
                            if (subBuffer != null && supBuffer != null && subBuffer.length() > 0) {
                                subBuffer.deleteCharAt(subBuffer.length() - 1);
                                supBuffer.deleteCharAt(supBuffer.length() - 1);
                            }
                            row.createCell(18).setCellValue(subBuffer.toString());
                            //主事项属性
                            row.createCell(19).setCellValue(supBuffer.toString());
                        }

                        //流程节点设置属性显隐
                        row.createCell(20).setCellValue("尚未填充");
                        //合计属性
                        row.createCell(21).setCellValue(listBasicInfo.getString("summationAttribute"));
                        //关联清册展示方式
                        row.createCell(22).setCellValue(listBasicInfo.getIntValue("isShowType") == 0 ? "标签" : "弹框");
                        //是否自动清空（刷新）清册数据
                        row.createCell(23).setCellValue(listBasicInfo.getIntValue("isAutomaticRemoveData") == 1 ? "是" : "否");
                        //是否配置清册数据勾选功能
                        int isConfigCheckListData = listBasicInfo.getIntValue("isConfigCheckListData");
                        String isCheck = "";
                        if (isConfigCheckListData == 0) {
                            isCheck = "否";
                        } else if (isConfigCheckListData == 1) {
                            isCheck = "单选";
                        } else if (isConfigCheckListData == 2) {
                            isCheck = "多选";
                        } else {
                            isCheck = "——";
                        }
                        row.createCell(24).setCellValue(isCheck);
                        //数据范围
                        row.createCell(25).setCellValue("尚未填充");
                        //清册列操作按钮校验规则
                        row.createCell(26).setCellValue(listBasicInfo.getString("buttonRuleName"));
                        rowNum++;
                    }
                }
            }
        }
    }

    /**
     * 获取对象定义属性
     */
    public static JSONArray getManageObjectProperties(XSSFSheet sheet, String str) throws IOException {
        String url = "https://appcs.jbysoft.com/SXDY/business/common/matterDefinitionCombine$m=query.service";
        String result = sentPost(url, str);
        JSONObject responseObject = JSONObject.parseObject(result);
        JSONArray outArray = responseObject.getJSONArray("results");
        //填充表数据
        //单元格往下移
        if (outArray != null && outArray.size() > 0) {
            JSONArray attributes = outArray.getJSONObject(0).getJSONArray(ConstantUtils.Attribute);
            for (int i = 0; i < attributes.size(); i++) {
                JSONObject attributej = attributes.getJSONObject(i);
                JSONArray attribute = attributej.getJSONArray(ConstantUtils.Attribute);
                sheet.shiftRows(rowNum, sheet.getLastRowNum(), attribute.size());
                for (int j = 0; j < attribute.size(); j++) {
                    JSONObject bj = attribute.getJSONObject(j);
                    JSONObject at = attribute.getJSONObject(j).getJSONObject(ConstantUtils.AttributeType);
                    XSSFRow row = null;
                    row = sheet.createRow(rowNum);
                    int rowNUM = 3;
                    //属性名称
                    XSSFCell cell3 = row.createCell(rowNUM++);
                    cell3.setCellValue(bj.getString(ConstantUtils.FieldName));
                    //属性别名
                    XSSFCell cell4 = row.createCell(rowNUM++);
                    cell4.setCellValue(bj.getString(ConstantUtils.Alias));
                    //分步名称
                    XSSFCell cell5 = row.createCell(rowNUM++);
                    cell5.setCellValue(ConstantUtils.DoubleLine);
                    //是否显示
                    XSSFCell cell7 = row.createCell(rowNUM++);
                    cell7.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfXS)));
                    //是否编辑
                    XSSFCell cell8 = row.createCell(rowNUM++);
                    cell8.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfBJ)));
                    //是否必填
                    XSSFCell cell9 = row.createCell(rowNUM++);
                    cell9.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfBt)));
                    //是否修改算法
                    XSSFCell cell10 = row.createCell(rowNUM++);
                    cell10.setCellValue(fillAlgorith(cell10, bj, attribute, at.getInteger(ConstantUtils.SfXGSF)));
                    //默认值
                    XSSFCell cell11 = row.createCell(11);
                    cell11.setCellValue(ConstantUtils.DoubleLine);
                    //提示语
                    XSSFCell cell12 = row.createCell(12);
                    cell12.setCellValue(ConstantUtils.DoubleLine);
                    //是否作为流程参数（分支或消息提醒
                    XSSFCell cell13 = row.createCell(13);
                    cell13.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SFZwLXfZTJ)));
                    //是否作为审批规则参数
                    XSSFCell cell14 = row.createCell(14);
                    cell14.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SFZwLXfZTJ)));
                    //是否作为审批规则参数
                    XSSFCell cell15 = row.createCell(15);
                    cell15.setCellValue(ConstantUtils.SfBT0);
                    //流程节点设置属性
                    XSSFCell cell16 = row.createCell(16);
                    cell16.setCellValue(assertISMustWriter(at.getJSONArray(ConstantUtils.LCJDSXDATE)));
                    //是否在变更清册中展示
                    XSSFCell cell17 = row.createCell(17);
                    cell17.setCellValue(ConstantUtils.DoubleLine);
                    //登录信息
                    XSSFCell cell18 = row.createCell(18);
                    cell18.setCellValue(Assert__(bj.getInteger(ConstantUtils.LoginInfo), hashMap));
                    //联想搜索显示属性
                    XSSFCell cell19 = row.createCell(19);
                    cell19.setCellValue(Assert__(bj.getString(ConstantUtils.AssociationDisplayContent)));
                    //联想查询类型
                    XSSFCell cell20 = row.createCell(20);
                    cell20.setCellValue(ConstantUtils.DoubleLine);
                    //画像跳转
                    XSSFCell cell21 = row.createCell(21);
                    cell21.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SFTZHX)));
                    //办理规则
                    XSSFCell cell22 = row.createCell(22);
                    cell22.setCellValue(ConstantUtils.SfBT0);
                    //是否作为公共属性
                    XSSFCell cell23 = row.createCell(23);
                    cell23.setCellValue(ConstantUtils.SfBT0);
                    //属性渲染列宽
                    XSSFCell cell24 = row.createCell(24);
                    cell24.setCellValue(ConstantUtils.DoubleLine);
                    //是否修改为互联互通
                    XSSFCell cell25 = row.createCell(25);
                    cell25.setCellValue(ConstantUtils.DoubleLine);
                    //是否显示互联互通按钮
                    XSSFCell cell26 = row.createCell(26);
                    cell26.setCellValue(ConstantUtils.DoubleLine);
                    //是否配置调用条件
                    XSSFCell cell27 = row.createCell(27);
                    cell27.setCellValue(ConstantUtils.DoubleLine);
                    //是否显示放大镜图标
                    XSSFCell cell28 = row.createCell(28);
                    cell28.setCellValue(assertISMustWriter(bj.getInteger(ConstantUtils.SFXSFDJTB)));
                    //是否配置时间范围
                    XSSFCell cell29 = row.createCell(29);
                    cell29.setCellValue(ConstantUtils.DoubleLine);
                    //数据范围
                    XSSFCell cell30 = row.createCell(30);
                    cell30.setCellValue(ConstantUtils.DoubleLine);
                    //属性样式算法
                    XSSFCell cell31 = row.createCell(31);
                    cell31.setCellValue(ConstantUtils.DoubleLine);
                    //属性占位列数
                    XSSFCell cell32 = row.createCell(32);
                    cell32.setCellValue(ConstantUtils.DoubleLine);
                    //下拉框展示方式
                    XSSFCell cell33 = row.createCell(33);
                    cell33.setCellValue(ConstantUtils.DoubleLine);
                    //事项拥有属性和其他对象属性赋值关系
                    XSSFCell cell34 = row.createCell(34);
                    cell34.setCellValue(ConstantUtils.DoubleLine);
                    //集成读卡设备
                    XSSFCell cell35 = row.createCell(35);
                    cell35.setCellValue(ConstantUtils.DoubleLine);
                    //读卡属性和事项拥有属性赋值关系
                    XSSFCell cell36 = row.createCell(36);
                    cell36.setCellValue(ConstantUtils.DoubleLine);
                    //是否加密
                    XSSFCell cell37 = row.createCell(37);
                    cell37.setCellValue(ConstantUtils.DoubleLine);
                    //是否加签
                    XSSFCell cell38 = row.createCell(38);
                    cell38.setCellValue(ConstantUtils.DoubleLine);
                    rowNum++;
                }
            }
        }
        return outArray;
    }

    /* *//**对象名称	属性别名	属性值		被影响的属性名称		是否显示	是否可编辑	是否必填	"是否作为流程参数（分支或消息提醒）
     "	是否作为审批规则参数	流程节点属性设置	属性占位列数
     * 获取对象定义属性
     *//*
    public static void getManageObjectProperties2(HSSFSheet sheet,String str) throws IOException {
        JSONObject responseObject = JSONObject.parseObject(str);
        //填充表数据
        if(responseObject.getBoolean("success")) {
            JSONArray outArray = responseObject.getJSONArray("results");
            JSONArray attributes = outArray.getJSONObject(0).getJSONArray(ConstantUtils.Attribute);
            int rowNum = 1;
            for (int i = 0; i <attributes.size() ; i++) {
                JSONObject attributej = attributes.getJSONObject(i);
                JSONArray attributeP = attributej.getJSONArray(ConstantUtils.Attribute);
                for (int j = 0; j <attributeP.size() ; j++) {
                    JSONArray objects = attributeP.getJSONArray(j);
                    if(objects ==null){
                        return;
                    }
                    for (int k = 0; k <objects.size() ; k++) {
                        JSONObject bj = attributeP.getJSONObject(k);
                        JSONObject at = attributeP.getJSONObject(k).getJSONObject(ConstantUtils.AttributeType);
                        HSSFRow row = sheet.createRow(rowNum);

                        //属性名称
                        XSSFCell cell1 = row.createCell(1);
                        cell1.setCellValue(bj.getString(ConstantUtils.YXSX));

                        //对象名称
                        XSSFCell cell2 = row.createCell(2);
                        cell2.setCellValue(bj.getString(ConstantUtils.YXSX));
                        //属性名称
                        XSSFCell cell3 = row.createCell(3);
                        cell3.setCellValue(bj.getString(ConstantUtils.FieldName));

                        //属性值
                        String string = at.getJSONArray("sxdymrz").getJSONObject(0).getString("name");
                        XSSFCell cell4 = row.createCell(4);
                        cell4.setCellValue(bj.getString(ConstantUtils.Alias));
                        //分步名称
                        XSSFCell cell5 = row.createCell(5);
                        cell5.setCellValue(ConstantUtils.DoubleLine);
                        //是否支持模糊搜索
                        XSSFCell cell6 = row.createCell(6);
                        cell6.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.IsFuzzySearch)));
                        //是否显示
                        XSSFCell cell7 = row.createCell(7);
                        cell7.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfXS)));
                        //是否编辑
                        XSSFCell cell8 = row.createCell(8);
                        cell8.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfBJ)));
                        //是否编辑
                        XSSFCell cell9 = row.createCell(9);
                        cell9.setCellValue(assertISMustWriter(at.getInteger(ConstantUtils.SfBt)));
                        rowNum++;
                    }
                }
            }
        }
    }*/

    /**
     * 判断是否必填是否显示
     */
    public static String assertISMustWriter(Integer s) {
        if (s == null) {
            return "否";
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
     * 判断是否必填是否显示
     */
    public static String assertISMustWriter(JSONArray jsonArray) {
        if (jsonArray == null) {
            return "否";
        }
        if (jsonArray.size() <= 0) {
            return ConstantUtils.SfBT0;
        } else {
            return ConstantUtils.SfBT1;
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
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 算法填充
     *
     * @param cell
     * @param bj
     */
    public static String fillAlgorith(XSSFCell cell, JSONObject bj, JSONArray array, Integer flag) {
        if (flag == null) {
            return ConstantUtils.SfBT0;
        }
        if (flag == 0) {
            return ConstantUtils.SfBT0;
        }
        StringBuilder sb = new StringBuilder();
        String attributes = bj.getString(ConstantUtils.AlgorithmAttributeSource);
        Integer backExecute = bj.getInteger(ConstantUtils.IsAlgorithmBackgroundExecution);
        sb.append(ConstantUtils.SfBT1);
        sb.append("，算法触发属性：");
        sb.append(setAttribute(attributes, array));
        sb.append(";");
        sb.append("是否后台执行：");
        sb.append(assertISMustWriter(backExecute));
        return sb.toString();
    }

    public static String setAttribute(String str, JSONArray array) {
        String[] split = str.split(",");
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> hmp = new HashMap<>(64);
        for (int j = 0; j < array.size(); j++) {
            JSONObject jsonObject = array.getJSONObject(j);
            String fdf = jsonObject.getString(ConstantUtils.FieldIdentification);
            String fn = jsonObject.getString(ConstantUtils.FieldName);
            if (fdf != null) {
                hmp.put(fdf, fn);
            }
        }
        for (int i = 0; i < split.length; i++) {
            if (hmp.containsKey(split[i])) {
                if (i == split.length - 1) {
                    sb.append(hmp.get(split[i]));
                } else {
                    sb.append(hmp.get(split[i]));
                    sb.append("，");
                }

            }
        }
        return sb.toString();
    }

    /**
     * 获取登录信息数组
     *
     * @return
     */
    private static HashMap searchLoginMessage(HashMap map) throws IOException {

        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/platformconfiguration$m=query.service";
        String body = "{\"type\":\"sxdy_dlxx\"}";
        String s = HttpUtils.doPost(url, map, body);
        HashMap hashMap = new HashMap(16);
        JSONObject responseObject = JSONObject.parseObject(s);
        if (responseObject.getBoolean("success")) {
            JSONArray outArray = responseObject.getJSONArray("results");
            for (int i = 0; i < outArray.size(); i++) {
                JSONObject jsonObject = outArray.getJSONObject(i);
                String num = jsonObject.getString("num");
                String name = jsonObject.getString("name");
                if (num != null) {
                    hashMap.put(num, name);
                }
            }
        }
        return hashMap;
    }


    /**
     * 判断是否需要插入--
     *
     * @param str
     * @return
     */
    private static String Assert__(String str) {
        if (str == null || str.trim().length()<=0) {
            return ConstantUtils.DoubleLine;
        } else {
            return str;
        }
    }

    private static String Assert__(Integer integer, HashMap map) throws IOException {

        if (integer == null) {
            return ConstantUtils.DoubleLine;
        } else {
            if (hashMap == null) {
                hashMap = searchLoginMessage(map);
            }

            Object o = hashMap.get(String.valueOf(integer));
            if (o != null) {
                return o.toString();
            }
        }
        return ConstantUtils.DoubleLine;
    }

    /**
     * 发送请求
     */
    public static String sentPost(String url, String body) {
        try {
            String result = HttpUtils.doPost(url, hashMap, body);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static XSSFRow setBasicParam(XSSFSheet sheet, XSSFRow row, JSONObject param, int beginCell) {
        //是否显示
        XSSFCell cell11 = row.createCell(beginCell);
        System.out.println("cell"+cell11);
        System.out.println("p"+param);
        if (param != null){
            cell11.setCellValue(GenerateObjectService.assertISMustWriter(param.getInteger(ConstantUtils.SfXS)));

            //是否可编辑
            XSSFCell cell13 = row.createCell(++beginCell);
            cell13.setCellValue(GenerateObjectService.assertISMustWriter(param.getInteger(ConstantUtils.SfBJ)));

            //是否必填
            XSSFCell cell10 = row.createCell(++beginCell);
            cell10.setCellValue(GenerateObjectService.assertISMustWriter(param.getInteger(ConstantUtils.SfBt)));
            //是否作为流程参数（分支或消息提醒）
            XSSFCell cell14 = row.createCell(++beginCell);
            cell14.setCellValue(assertISMustWriter(param.getInteger(ConstantUtils.SFZwLXfZTJ)));

            //是否作为审批规则参数
            XSSFCell cell15 = row.createCell(++beginCell);
            cell15.setCellValue(assertISMustWriter(param.getInteger(ConstantUtils.SFZwLXfZTJ)));

            //流程节点属性设置
            XSSFCell cell16 = row.createCell(++beginCell);
            cell16.setCellValue("暂未填充");


            //流程节点属性设置
            XSSFCell cell17 = row.createCell(++beginCell);
            cell17.setCellValue("暂未填充");
        }
        return row;
    }

    public static int getListNumber(JSONArray kzzdList) {
        JSONObject beforeObj = kzzdList.getJSONObject(0);
        String target = beforeObj.getString("fieldName");
        int g = 0;
        boolean flag = true;
        while (flag) {
            g++;
            String s = kzzdList.getJSONObject(g).getString("fieldName");
            if (!target.equals(s)) {
                flag = false;
            }
            if (kzzdList.size() - 1 == g) {
                flag = false;
            }
        }
        return g;
    }

    //影响属性数据处理
    public static HashMap<String, Object> getListNumberNew(JSONArray kzzdList, String fieldIdentification, String body) {
        JSONObject object = JSONObject.parseObject(body);
        object.put("fieldIdentification", fieldIdentification);
        String url = "https://appcs.jbysoft.com/V2/GLDX/business/common/objectAttributeOptionScope$m=query.service";
        String s = sentPost(url, object.toJSONString());
        System.out.println(s);
        JSONArray results = JSONObject.parseObject(s).getJSONArray("results");
        ArrayList<JSONObject> arrays = new ArrayList<>(kzzdList.size());
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        if (results == null && results.size() == 0) {
            return null;
        }
        for (int i = 0; i < results.size(); i++) {
            int sqNum = results.getJSONObject(i).getIntValue("sequenceNumber");
            for (int j = 0; j < kzzdList.size(); j++) {
                JSONObject object1 = kzzdList.getJSONObject(j);
                int ctlFileNum = object1.getIntValue("controlFieldNumber");
                if (sqNum == ctlFileNum) {
                    object1.put("name", results.getJSONObject(i).getString("name"));
                    arrays.add(object1);
                }
            }
        }
        objectObjectHashMap.put("con", arrays);
        objectObjectHashMap.put("num", results.size());
        return objectObjectHashMap;
    }
}
