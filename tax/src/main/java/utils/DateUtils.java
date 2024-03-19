package utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description:DateUtils
 * @author:pxf
 * @data:2023/04/27
 **/
public class DateUtils {
    public static Map dateSP(Map param){
        Object dataObj  = param.get("date");
        if(dataObj == null && Objects.isNull(dataObj)){
            return  param;
        }
        if (dataObj instanceof ArrayList){
        ArrayList dates = (ArrayList)dataObj;
        String  beginDate = dates.get(0).toString();
        String endStr =dates.get(1).toString();
        param.put("beginDate",beginDate);
        param.put("endDate",endStr);
        }
        return  param;
    }
    public static Date formatDate(String str){
        SimpleDateFormat sf = new SimpleDateFormat(str);
        String format = sf.format(new Date());
        Date parse = null;
        try {
           parse = sf.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("时间格式化失败");
        }
        return  parse;
    }
}
