package factory.simplefactory;

/**
 * @description:Fighter
 * @author:pxf
 * @data:2023/07/26
 **/
public class Fighter extends Weapon{
    @Override
    public void attack() {
        System.out.println("战斗机在开火！");
    }
}
