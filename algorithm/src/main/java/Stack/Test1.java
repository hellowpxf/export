package Stack;

import java.util.Stack;

/**
 * @description:Test1
 * @author:pxf
 * @data:2023/03/01
 **/
public class Test1 {
    public static void main(String[] args) {
        System.out.println(facet(6));
        System.out.println(stackMultiple(6));
        System.out.println(foLoop(6));
    }
    /**递归求阶乘*/
    public static int facet(int param){
        if(param<=1){
            return param;
        }
        return  param * facet(param-1);
    }
    /**用栈求阶乘*/
    public static int stackMultiple(int param){
        Stack<Integer> ints = new Stack<>();
        while (param>1){
            ints.add(param--);
        }
        int result =1;
        while (!ints.isEmpty()){
         result *= ints.pop();
        }
        return result;
    }
    /**用forLoop求阶乘**/
    public static  int foLoop(int param){
        int result =1;
        for (int i =1;i<=param;i++){
            result = i*result;
        }
        return  result;
    }

}
