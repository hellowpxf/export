package factory.simplefactory;

/**
 * @description:Tank
 * @author:pxf
 * @data:2023/07/26
 **/
public class Tank extends Weapon {
    @Override
    public void attack() {
        System.out.println("tank Shutting");
    }
}
