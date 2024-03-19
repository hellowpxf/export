package factory.factorymethod;

import org.springframework.beans.factory.FactoryBean;

/**
 * @description:Gun
 * @author:pxf
 * @data:2023/07/26
 **/
public class Gun extends Weapon implements FactoryBean {
    @Override
    public void attack() {
        System.out.println("drop the gun!");
    }

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
