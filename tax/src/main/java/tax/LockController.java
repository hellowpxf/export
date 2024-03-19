package tax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @description:LockController
 * @author:pxf
 * @data:2023/10/19
 **/
@Controller
public class LockController {
    @Autowired
    private LockAboutRedis lockAboutRedis;
    @RequestMapping(value = "/lock", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
    public boolean login() {
        try {
            lockAboutRedis.distributeLock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  false;
    }
}
