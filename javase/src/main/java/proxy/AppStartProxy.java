package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @description:AppStartProxy
 * @author:pxf
 * @data:2023/03/02
 **/
/**中介类**/
public class AppStartProxy implements InvocationHandler {

    Object object;

    public AppStartProxy(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("begin record log");
        Object result = method.invoke(object, args);
        System.out.println("log received success");
        return result;
    }
}
