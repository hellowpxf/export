package factory.simplefactory;

/**
 * @description:Dagger
 * @author:pxf
 * @data:2023/07/26
 **/
public class Dagger extends Weapon{
    @Override
    public void attack() {
        System.out.println("砍他!");
    }
}
