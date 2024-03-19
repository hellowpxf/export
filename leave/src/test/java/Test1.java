
/**
 * @description:Test1
 * @author:pxf
 * @data:2023/11/22
 **/
public class Test1 {
    boolean flag = false; // 共享变量

    synchronized void waitForFlagChange() {
        while (!flag) {
            try {
                wait(); // 等待直到条件满足
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName());
        System.out.println("Flag has been changed2!");
    }

    synchronized void changeFlag() {
        flag = true; // 修改共享变量
        notify(); // 唤醒等待的线程
        System.out.println(Thread.currentThread().getName());
        System.out.println("Flag has been changed1!");
    }
}

class Main {
    public static void main(String[] args) {
        int test = Main.test();
        System.out.println("test"+test);

    }
    public  static  int test(){
        int i = 0;
        try {
            i++;
            String  s = null;
            s.length();
            System.out.println("try"+i);
            return i;
        }catch (Exception e){
            i++;
            System.out.println("catch"+i);
            return i;
        }finally {
            i++;
            System.out.println("finally"+i);
            return i;
        }
    }
}
