package com.controller;

import com.dao.Vacation;
import com.serviceImp.VacationServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:VocationController
 * @author:pxf
 * @data:2023/11/20
 **/
@Controller
@RequestMapping("/leave")
public class VacationController {
    @Autowired
    private VacationServiceImp vacationServiceImp;
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public Vacation generateObject(@RequestParam("id") int id) {

        try {
            Vacation vacation = vacationServiceImp.getLeaveById(id);
            return  vacation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
