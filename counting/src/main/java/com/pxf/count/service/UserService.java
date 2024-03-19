package com.pxf.count.service;

/**
 * @description:UserService
 * @author:pxf
 * @data:2024/03/01
 **/
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.pxf.count.dao.User;
import com.pxf.count.mapper.UserMapper;
import com.pxf.count.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @SentinelResource("registerUser")
    public Result registerUser(User user) {
        Result result = new Result();
        Long phoneNumber = user.getPhoneNumber();
        if (phoneNumber == null){
            result.setMsg("电话号码不能为空");
            return  result;
        }
       int count = userMapper.findByPhoneNumber(user.getPhoneNumber());
       if (count>0){
           result.setMsg("电话号码重复注册失败");
           return  result;
       }else  {
          userMapper.insert(user);
       }
        result.setMsg("恭喜您注册成功");
        return  result;
    }

    public User loginUser(User user) {
        return userMapper.findByUsernameAndPassword(user);
    }

}
