package tax;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @description:getTaxNumberController
 * @author:pxf
 * @data:2023/04/12
 **/
@Controller
@RequestMapping("/tax")
public class GetTaxNumberController {
    private static final Logger log = Logger.getLogger(GetTaxNumberController.class);
    @Autowired
    private GetTaxService getTaxService;
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
    public Object generateObject(@RequestParam("keyword") String keyword) {
        try {
            log.info("打印入参"+keyword);
            JSONObject taxNumberList = getTaxService.getTaxNumberList(keyword);
            return  taxNumberList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public int TaxMessageAdd(@RequestBody Tax tax) {
        try {
            System.out.println("打印传参");
            System.out.println(tax.toString());
            getTaxService.addTaxMessage(tax);
            return  200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  500;
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public Object TaxMessageGet(@RequestBody Map param) {
        try {
            System.out.println("打印传参");
            param.keySet().stream().forEach(key ->{
                System.out.println(key + " : " + param.get(key));
            });
            List<Tax> taxList = getTaxService.getTaxMessage(param);
            return  taxList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    @RequestMapping(value = "/export", method = RequestMethod.POST)
    @ResponseBody
    public Object generateSubject(@RequestBody Map param,HttpServletResponse response) {
        try {
            System.out.println("打印导出传参");
            System.out.println(param);
            getTaxService.exportMessage(param,response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }


    @RequestMapping(value = "/getTurnover", method = RequestMethod.POST)
    @ResponseBody
    public Object getTurnover(@RequestBody Map param) {
        try {
            System.out.println("打印传参");
            param.keySet().stream().forEach(key ->{
                System.out.println(key + " : " + param.get(key));
            });
            List<Tax> taxList = getTaxService.getTurnover(param);
            return  taxList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}

