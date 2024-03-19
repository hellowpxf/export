package document;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @description:getTaxNumberController
 * @author:pxf
 * @data:2023/04/12
 **/
@Controller
@RequestMapping("/tax")
public class GetTaxNumberController {
    @Autowired
    private GetTaxService getTaxService;
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public Object generateObject(@RequestParam("keyword") String keyword,@RequestParam("sid") String sid) {
        try {
            System.out.println("打印传参");
            System.out.println(keyword +" ==" +sid);
            JSONObject taxNumberList = getTaxService.getTaxNumberList(keyword);
            return  taxNumberList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
