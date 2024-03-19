import com.alibaba.fastjson.JSONObject;

import java.awt.image.PackedColorModel;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

/**
 * @description:TestSimple
 * @author:pxf
 * @data:2022/10/27
 **/
public class TestSimple {
        int petalCount ;
        String s = new String("null");
        TestSimple(int petals){
        this.petalCount = petals;
            System.out.println(petalCount +"in arg");
        }
        TestSimple(String ss){
         this.s = ss;
        }
        TestSimple(String s, int petals) {
         this(petals);
            System.out.println(s);
        }

    public static void main(String[] args) {
        //TestSimple testSimple = new TestSimple("hello",6);
        int []param = {10,5,6,7,82,3,5,45};
        binarySort(param,0,param.length-1);
        //get_mid(param,0,param.length-1);
    }
    public static void binarySort(int s[], int l, int r){
        {
            int i = l, j = r;
            int x = s[l]; //s[l]即s[i]就是第一个坑
            while (i < j)
            {
                // 从右向左找小于x的数来填s[i]
                while(i < j && s[j] >= x)
                    j--;
                if(i < j)
                {
                    s[i] = s[j]; //将s[j]填到s[i]中，s[j]就形成了一个新的坑
                    i++;
                }

                // 从左向右找大于或等于x的数来填s[j]
                while(i < j && s[i] < x)
                    i++;
                if(i < j)
                {
                    s[j] = s[i]; //将s[i]填到s[j]中，s[i]就形成了一个新的坑
                    j--;
                }
            }
            //退出时，i等于j。将x填到这个坑中。
            s[i] = x;
            System.out.println(Arrays.toString(s));
            System.out.println(x);
            return ;
        }
    }
    private static int get_mid(int arr[],int left,int right){
        int pivot=arr[left];//自定义排序中心轴，这里把arr[left]存到pivot中去，此时arr[left]为空。pivot相当于一个中间量
        while(left<right){//当left与right指针相遇的时候退出循环，双指针遍历结束
            while(arr[right]>=pivot && left<right) right--;//right指针从右往左遍历，当arr[right]>=pivot，即满足以pivot为中轴，小放左，大放右的条件时，right指针继续往右遍历。当arr[right]<pivotd的时候，把当前值arr[right]赋给空置arr[left]，此时arr[right]成了空值。
            arr[left]=arr[right];
            while(arr[left]<=pivot && left<right) left++;//到left指针从左往右遍历，当arr[left]<=pivot，即满足以pivot为中轴，小放左，大放右的条件时，left指针继续往左遍历。当arr[left]>pivot的时候，把当前值arr[left]赋给空置arr[right]，此时arr[left]成了空值。
            arr[right]=arr[left];
        }
        //经历了上面的循环实现了pivot为中轴，小放左，大放右的格局
        arr[left]=pivot;//最后把存放在pivot值放回数组空arr[left]中
        System.out.println(left);
        return left;//返回中轴所在的下标位置。
    }

}
