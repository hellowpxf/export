package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @description:UserProxy
 * @author:pxf
 * @data:2023/07/26
 **/
public class UserProxy implements InvocationHandler {
    private  Object target;
    public UserProxy(Object target){
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long  begin = System.currentTimeMillis();
        Object retValue = method.invoke(target,args);
        Thread.sleep(1000);
        long end = System.currentTimeMillis();
        System.out.println("耗时"+(end-begin));
        return  retValue;
    }
}
