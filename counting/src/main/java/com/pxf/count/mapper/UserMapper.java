package com.pxf.count.mapper;

import com.pxf.count.dao.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users (username, password,phoneNumber) VALUES (#{username}, #{password},#{phoneNumber})")
    void insert(User user);


    @Select("SELECT * FROM users WHERE username = #{user.getUsername()} AND password = #{user.getPassword()}  AND phoneNumber = #{user.getPhoneNumber()}")
            User findByUsernameAndPassword(@Param("user") User user);

    @Select("SELECT count(*)  FROM users WHERE  phoneNumber = #{phoneNumber}")
    int findByPhoneNumber(@Param("phoneNumber") Long phoneNumber);
}
