package document;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 * @author Administrator
 */
public interface GenerateService {
    /**
     * 属性基本参数设置
     * @param row
     * @param param
     * @param beginCell
     * @return
     */
    XSSFRow setBasicParam(XSSFRow row, JSONObject param, int beginCell);
}
