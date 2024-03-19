package proxy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @description:MyAspectJ
 * @author:pxf
 * @data:2023/08/17
 **/
@Aspect
public class MyAspectJ {
    @Before("execution(* proxy.UserServiceImp.eat(..))")
    public void enhanceAdvicePrint(){
        System.out.println("hello ");
    }
}
