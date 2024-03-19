package com.pxf.count.controller;

/**
 * @description:UserController
 * @author:pxf
 * @data:2024/03/01
 **/
import com.pxf.count.dao.User;
import com.pxf.count.service.SentinelTest;
import com.pxf.count.service.UserService;
import com.pxf.count.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private SentinelTest sentinelTest;

    @PostMapping("/register")
    public Result registerUser(@RequestBody User user) {
      return  userService.registerUser(user);
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody User user) {
        return userService.loginUser(user);
    }
    @GetMapping("/query")
    public String queryUser(@RequestParam String id) {
        return sentinelTest.getUserById(id);
    }
}