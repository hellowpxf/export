package service;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import dao.Fruit;
import dao.Orderdetails;
import dao.Orders;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.SqlSessionUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:ImportOrders
 * @author:pxf
 * @data:2023/02/22
 **/
public class ImportOrders implements OrdersService{
    @Override
    public int saveOrders(List<Orders> orders) {
        SqlSession sqlSession = SqlSessionUtils.getSession();
        System.out.println(sqlSession);
        sqlSession.insert("dao.Orders.insertOrders",orders);
        sqlSession.flushStatements();
        sqlSession.commit();
        SqlSessionUtils.closeSession();
        return  0;
    }

    public  List<Orders> importExcel() {
        List<Orders> list = null;
        try {
             list = new ArrayList<>();
            for (int i = 900000; i <= 1000000; i++) {
                Orders orders = new Orders();
                orders.setId(i);
                orders.setCustomerName("A"+i);
                orders.setOrderDate(new Date());
                list.add(orders);
            }
            //循环展示导入的数据，实际应用中应该校验并存入数据库
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
