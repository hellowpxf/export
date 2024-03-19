package com.serviceImp;

import com.dao.Vacation;
import com.service.VacationService;
import com.utils.SqlSessionUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

/**
 * @description:VacationServiceImp
 * @author:pxf
 * @data:2023/11/20
 **/
@Service
public class VacationServiceImp implements VacationService {
    @Override
    public void insertLeave(Vacation vacation) {
        
    }

    @Override
    public Vacation getLeaveById(int id) {
        SqlSession sqlSession = SqlSessionUtils.getSession();
        System.out.println(sqlSession);
        Vacation v = sqlSession.selectOne("dao.Vacation.getLeaveById",id);
        sqlSession.commit();
        SqlSessionUtils.closeSession();
        return  v;
    }

    @Override
    public void updateLeave(Vacation vacation) {

    }

    @Override
    public void deleteLeave(int id) {

         }
}
