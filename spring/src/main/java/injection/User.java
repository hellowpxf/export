package injection;

/**
 * @description:User
 * @author:pxf
 * @data:2023/07/20
 **/
public class User {
    private int age;
    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                '}';
    }

}
