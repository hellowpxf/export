package injection;

/**
 * @description:UserService
 * @author:pxf
 * @data:2023/07/19
 **/
public class UserService {
    private UserDao userDao;

    // 使用set方式注入，必须提供set方法。
    // 反射机制要调用这个方法给属性赋值的。
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(){
        userDao.insert();
    }
}
