package proxy;

/**
 * @description:UserServiceImp
 * @author:pxf
 * @data:2023/07/26
 **/
public class UserServiceImp implements UserService {
    @Override
    public void eat() {
        System.out.println("食饭啦！");
    }

    @Override
    public void jump() {
        System.out.println("jump");
    }
}
