package service;

import dao.Orderdetails;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import utils.SqlSessionUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * @description:ImportOrderdetails
 * @author:pxf
 * @data:2023/02/22
 **/
public class ImportOrderdetails implements OrderDetailsService {
    public  List<Orderdetails> importExcel() {
        List<Orderdetails> list = null;
        try {
            list = new ArrayList<>();
            for (int i = 1900000; i <= 2000000; i++) {
                Orderdetails ods = new Orderdetails();
                ods.setId(i);
                ods.setOrderId(75);
                ods.setProductCode("B"+i);
                ods.setQuantity((int)Math.random());
                list.add(ods);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int insertOrderDetails(List<Orderdetails> orderDetails) {
        SqlSession sqlSession = SqlSessionUtils.getSession();
        System.out.println(sqlSession);
        sqlSession.insert("dao.Orderdetails.insertOrderdetails",orderDetails);
        sqlSession.flushStatements();
        sqlSession.commit();
        SqlSessionUtils.closeSession();
        return  0;
    }

    @Override
    public int selectOrderDetail(Orderdetails orderDetail) {
        SqlSession sqlSession = SqlSessionUtils.getSession();
        System.out.println(sqlSession);
        sqlSession.select("dao.Orderdetails.insertOrderdetails", (ResultHandler) orderDetail);
        sqlSession.flushStatements();
        sqlSession.commit();
        SqlSessionUtils.closeSession();
        return  0;
    }
}
