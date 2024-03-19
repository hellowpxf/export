package factory.factorymethod;

/**
 * @description:GunFactory
 * @author:pxf
 * @data:2023/07/26
 **/
public class GunFactory implements WeaponFactory {
    @Override
    public Weapon get() {
        return new Gun();
    }
}
