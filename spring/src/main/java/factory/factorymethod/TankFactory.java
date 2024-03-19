package factory.factorymethod;

/**
 * @description:TankFactory
 * @author:pxf
 * @data:2023/07/26
 **/
public class TankFactory implements WeaponFactory {
    @Override
    public Weapon get() {
        return new Tank();
    }
}
