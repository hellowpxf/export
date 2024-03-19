import dao.Orderdetails;
import dao.Orders;
import org.junit.Test;
import service.ImportOrderdetails;
import service.ImportOrders;


import java.util.List;

/**
 * @description:Import
 * @author:pxf
 * @data:2022/11/19
 **/
public class Import {
    @Test
    public void test1(){
        long e = System.currentTimeMillis();
        ImportOrders importOrders = new ImportOrders();
        List<Orders> orders = importOrders.importExcel();
        importOrders.saveOrders(orders);
        long l = System.currentTimeMillis();
        System.out.println(l-e);
    }
    @Test
    public void test2(){
        long e = System.currentTimeMillis();
        ImportOrderdetails importOrderdetails = new ImportOrderdetails();
        List<Orderdetails> orderdetails = importOrderdetails.importExcel();
        importOrderdetails.insertOrderDetails(orderdetails);
        long l = System.currentTimeMillis();
        System.out.println(l-e);
    }
}
