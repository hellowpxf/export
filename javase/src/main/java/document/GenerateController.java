package document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:GenerateController
 * @author:pxf
 * @data:2023/03/18
 **/
@Controller
@RequestMapping("/generate")
public class GenerateController {
    @Autowired
    private GenerateObjectService remoteService;
    @Autowired
    private GenerateSubjectFileService generateSubjectFileService;
    @RequestMapping(value = "/object", method = RequestMethod.POST)
    @ResponseBody
    public Object generateObject1(@RequestBody String body, HttpServletResponse  response,HttpServletRequest request) {
        try {
            System.out.println(body);
            remoteService.generateCore1(body,response,request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @RequestMapping(value = "/object/content", method = RequestMethod.POST)
    @ResponseBody
    public Object generateObject(@RequestBody String body, HttpServletResponse  response,HttpServletRequest request) {
        try {
            System.out.println(body);
            remoteService.generateCore(body,response,request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Autowired
    private GenerateSubjectService subjectService;
    @RequestMapping(value = "/subject", method = RequestMethod.POST)
    @ResponseBody
    public Object generateSubject1(@RequestBody String body, HttpServletResponse  response, HttpServletRequest request) {
        try {
            System.out.println(body);
            generateSubjectFileService.generateCore(body,response,request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @RequestMapping(value = "/subject/content", method = RequestMethod.POST)
    @ResponseBody
    public Object generateSubject(@RequestBody String body, HttpServletResponse  response, HttpServletRequest request) {
        try {
            System.out.println(body);
            subjectService.generateCore(body,response,request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
