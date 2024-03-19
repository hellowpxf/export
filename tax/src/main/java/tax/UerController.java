package tax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @description:UerController
 * @author:pxf
 * @data:2023/04/28
 **/
@Controller
@RequestMapping("/user")
public class UerController {
    @Autowired
    private LoginService loginService;
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public boolean register(@RequestBody User user) {
        try {
            System.out.println("打印注册传参");
            System.out.println(user);
            boolean flag  = loginService.register(user);
            return  flag;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("账号注册失败");
        }
        return  false;
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public boolean login(@RequestBody User user) {
        try {
            System.out.println("打印注册传参");
            System.out.println(user);
            boolean flag  = loginService.login(user);
            return  flag;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("登录失败");
        }
        return  false;
    }
}
