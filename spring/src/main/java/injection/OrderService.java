package injection;

/**
 * @description:OrderService
 * @author:pxf
 * @data:2023/07/20
 **/
public class OrderService {
    private OrderDao orderDao;
    private UserDao userDao;

    // 通过反射机制调用构造方法给属性赋值
    public OrderService(OrderDao orderDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.userDao = userDao;
    }



    public void delete(){
        orderDao.deleteById();
        userDao.insert();
    }
}
