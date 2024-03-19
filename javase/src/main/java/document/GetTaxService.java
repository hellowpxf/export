package document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import utils.HttpUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * @description:GetTaxService
 * @author:pxf
 * @data:2023/04/12
 **/
@Service
public class GetTaxService {
    public JSONObject getTaxNumberList(String keyWord) throws IOException {
        HashMap<String, String> header = new HashMap<>();
        header.put("keyword",keyWord);
        String url = "https://payapp.wechatpay.cn/invoicing/userweb/invoicetitle/search"+"?keyword="+keyWord+"&page=1&page_size=10";
        String s = HttpUtils.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(s);

        JSONObject body = new JSONObject();
        body.put("jgbh", "1301100001");
        body.put("dx_03_dxbh", "'009000000017123','009000000015449','009000000015980','009000000016469','009000000014279','009000000017093','009000000016310','009000000016588','009000000014894','009000000015852','009000000016168','009000000016443','009000000016453','009000000014118','009000000015977','009000000016162','01000000009000026011','01000000009000026004','01000000009000029628','01000000009000026492','0100000000900002961'");



        return  jsonObject;
    }
}
