package factory.simplefactory;

/**
 * @description:WeaponFactory
 * @author:pxf
 * @data:2023/07/26
 **/
public class WeaponFactory {
    public static Weapon get(String weaponType){
        if (weaponType == null || weaponType.trim().length() ==0){
            return  null;
        }
        Weapon weapon = null;
        if ("TANK".equals(weaponType)){
            weapon = new Tank();
        }else if("Dagger".equals(weaponType)){
            weapon = new Dagger();
        }else if ("Fighter".equals(weaponType)){
            weapon = new Fighter();
        }else {
            throw  new RuntimeException("不支持该武器");
        }
        return  weapon;
    }
}
