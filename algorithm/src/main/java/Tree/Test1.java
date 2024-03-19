package Tree;

import java.util.LinkedList;
import java.util.Stack;

/**
 * @description:Test1
 * @author:pxf
 * @data:2023/03/01
 **/
public class Test1 {
    public static void main(String[] args) {
        LinkedList<Object> objects = new LinkedList<>();
        objects.add("2");
        Node node = new Node(5);
        node.left=new Node(4);
        node.left.left=new Node(2);
        node.left.right = new Node(3);
        node.right = new Node(7);
        node.right.left = new Node(6);
        node.right.right = new Node(8);
        loopTree(node);
        //loopTreeByStack(node);
    }
    public static class Node{
        public int value;
        public Node right;
        public Node left;

        public Node(int value) {
            this.value = value;
        }
    }

    public static String beautyDailyLog(String e) {
        return  null;
    }
    public static void loopTree(Node node){
        if(node ==null){
            return;
        }
        loopTree(node.left);
        System.out.println(node.value);
        loopTree(node.right);
    }
    /*先序:中左右*/
    public static  void   loopTreeByStackFir(Node node){
        if(node !=null) {
            Stack<Node> nodes = new Stack<>();
            nodes.add(node);
            while (!nodes.isEmpty()) {
                node = nodes.pop();
                System.out.println(node.value);
                if (node.right != null) {
                    nodes.push(node.right);
                }
                if (node.left != null) {
                    nodes.push(node.left);
                }
            }
        }
    }
    /*中序:左中右*/
    public static  void   loopTreeByStackM(Node node){
        if(node !=null) {
            Stack<Node> nodes = new Stack<>();
            nodes.add(node);
            while (!nodes.isEmpty()) {
                node = nodes.pop();
                System.out.println(node.value);
                if (node.right != null) {
                    nodes.push(node.right);
                }
                if (node.left != null) {
                    nodes.push(node.left);
                }
            }
        }
    }
}
