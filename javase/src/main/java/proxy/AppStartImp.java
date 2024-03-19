package proxy;

/**
 * @description:AppStartImp
 * @author:pxf
 * @data:2023/03/02
 **/
/**代理类**/
public class AppStartImp implements AppStart {
    @Override
    public int startApp() {
        System.out.println("app  created");
        return 1;
    }

    @Override
    public int shutDownApp() {
        System.out.println("shut down app");
        return 0;
    }
}
