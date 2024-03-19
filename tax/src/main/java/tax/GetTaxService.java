package tax;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import utils.DateUtils;
import utils.ExcelUtils;
import utils.HttpUtils;
import utils.SqlSessionUtils;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @description:GetTaxService
 * @author:pxf
 * @data:2023/04/12
 **/
@Service
public class GetTaxService {
    @Autowired
    private RedisTemplate<Object,Object> rt;
    private static final Logger log = Logger.getLogger(GetTaxService.class);
    public JSONObject getTaxNumberList(String keyWord) throws IOException {
        HashMap<String, String> header = new HashMap<>();
        header.put("keyword",keyWord);
        String url = "https://payapp.wechatpay.cn/invoicing/userweb/invoicetitle/search"+"?keyword="+keyWord+"&page=1&page_size=10";
        String s = HttpUtils.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(s);
        log.info("taxNumber查询反参"+jsonObject);
        return  jsonObject;
    }

    @CacheEvict(value = "pc",allEntries = true)
    public  int  addTaxMessage (Tax tax){
        SqlSession sqlSession = SqlSessionUtils.getSession();
        Date date = new Date();
        tax.setDate(date);
        int flag = sqlSession.insert("tax.TaxMapper.addTax",tax);
        sqlSession.commit();
        SqlSessionUtils.closeSession();
        return  flag;
    }
    @Cacheable(value = "pc", key = "'tax_all'")
    public  List<Tax>  getTaxMessage (Map param){
        param = DateUtils.dateSP(param);
        System.out.println(param.toString());
        SqlSession sqlSession = SqlSessionUtils.getSession();
         List<Tax> taxes = sqlSession.selectList("tax.TaxMapper.selectAll",param);
        System.out.println(taxes);
        return  taxes;
    }

    public  List<Tax>  getTurnover (Map param){
        BoundValueOperations<Object,Object> ops  = rt.boundValueOps("turnover");
        Object turnover = ops.get();
        if (turnover == null){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            sdf.format(date);
            param = DateUtils.dateSP(param);
            System.out.println(param.toString());
            SqlSession sqlSession = SqlSessionUtils.getSession();
            turnover = sqlSession.selectList("tax.TaxMapper.selectAll",param);
            ops.set(turnover,20, TimeUnit.SECONDS);
        }
        System.out.println(turnover);
        return (List<Tax>) turnover;
    }

    public  boolean  exportMessage (Map param, HttpServletResponse response) throws IOException {
        SqlSession sqlSession = SqlSessionUtils.getSession();
        List<Tax> taxes = sqlSession.selectList("tax.TaxMapper.selectAll",param);
        System.out.println(taxes);
        //创建一个工作簿
        HSSFWorkbook sheets = new HSSFWorkbook();
        HSSFSheet sheet = sheets.createSheet();
        //设置表头
        ExcelUtils.setTableHeader(sheet);
        //包装数据
        ExcelUtils.getManageObjectProperties(sheet,taxes);
        //调整格式
        ExcelUtils.adjustmentCell(sheet,sheets);
        //声明输出流
        OutputStream os = null;
        //设置响应头
        ExcelUtils.setResponseHeader(response,"taxMessage.xlsx");
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
}
