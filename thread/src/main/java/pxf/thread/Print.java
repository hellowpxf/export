package pxf.thread;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:Print
 * @author:pxf
 * @data:2023/02/14
 **/
public class Print {
    public void print(String str) {
        System.out.println();
    }

    public static void main(String[] args) {

        int paramNumS [] = {4,9,3,1,5};
        //bubbleSort(paramNumS);
        //selectSort(paramNumS);
        //System.out.println(facet(6));
        //splitSort(selectSort(paramNumS),4);
        insertSort(paramNumS);

    }

// 冒泡排序
    public  static void bubbleSort(int [] params){
        int count  =0 ;
        for (int i = params.length-1; i >0 ; i--) {
            boolean flag  = true;
            for (int j = 0; j <i ; j++) {
                count++;
                if(params[j]>params[j+1]){
                    int temp = params[j];
                    params[j] = params[j+1];
                    params[j+1] = temp;
                    flag = false;
                }
            }
            if(flag){
                break;
            }
        }
        System.out.println(count);
        for (int i : params
             ) {
            System.out.println(i);
        }
    }

    /*选择排序
     * {4,9,3,1,5}
     * 第一次：4、5、3、1、9
     * 第二次：4、1、3、5
     * 第三次：。。。。。
     * */
    public  static int [] selectSort(int [] params){
        for (int i = params.length-1; i >0 ; i--) {
            int largeIndex = i;
            for (int j = 0; j <i ; j++) {
                if(params[j]>params[largeIndex]){
                    largeIndex = j;
                }
            }
            swap(params,i,largeIndex);
        }
        for (int i : params
        ) {
            System.out.println(i);
        }
        return  params;
    }
    /*二分查找法
    * */
    public static void splitSort(int [] param,int target){
        int l = 0;
        int r = param.length;
        int mid = (l+r)/2;
        while (l<=r){
            if(param[mid]<target){
                l++;
            }else if(param[mid]>target){
                r--;
            }else {
                System.out.println(mid);
                return;
            }
            mid = (l+r)/2;
        }
        System.out.println(-1);
        return;
    }
    /*插入排序
            int paramNumS [] = {4,9,3,1,5};
    * 第一遍：4,9,3,1,5
    * 第二部；3,4,9,1
    * 第三遍： 3,4,1,
    */
    public static void insertSort(int [] params){
        for (int i = 1; i <params.length ; i++) {
            int j = i;
            while (params[j]<params[j-1]){
                 swap(params,j-1,j);
                 j--;
                 if(j==0){
                     break;
                 }
            }
        }
        for (int i : params
             ) {
            System.out.println(i);
        }
    }

    /* 递归计算阶乘
    * */
    public static int  facet(int n){
        if(n<=1){
            return 1;
        }
        return  n*facet(n-1);
    }
//    swap
    public  static int [] swap(int [] params,int l,int r){
        int temp = params[r];
        params[r] = params[l];
        params[l] = temp;
        return  params;
    }

}
