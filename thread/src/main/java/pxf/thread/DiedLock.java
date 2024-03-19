package pxf.thread;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description:DiedLock
 * @author:pxf
 * @data:2023/02/14
 **/
public class DiedLock extends Thread {
    Dog dog1 = new Dog();
    Dog dog2 = new Dog();
    public DiedLock() {

    }

    /*死锁问题*/
    public static void main(String[] args) {
        Dog dog1 = new Dog();
        Dog dog2 = new Dog();
        Thread diedLock = new Thread(() -> {
            System.out.println("thread1 锁定 dog1");
            synchronized (dog1) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (dog2){
                    System.out.println("thread1 锁定 dog2");
                }
            }
        });
        diedLock.start();

        Thread diedLock2 = new Thread(() -> {
            System.out.println("thread2 锁定 dog2");
            synchronized (dog1) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (dog1){
                    System.out.println("thread2 锁定 dog1");
                }
            }
        });
        diedLock2.start();
    }


}
