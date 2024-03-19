package pxf.fundation.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ChatClient {
    public void startClient(String name ) throws IOException, InterruptedException {
        //        创建通道 绑定主机和端口
        SocketChannel socketChannel = null;
        socketChannel = SocketChannel.open(new InetSocketAddress(
                "127.0.0.1", 7890));


        //接受服务端响应的数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        //创建线程
        new Thread(new ClientThread(selector)).start();//负责拿到服务器端数据

        //向服务端发送数据
        System.out.println("请输入抄水表编号、抄水量、抄表员(抄水时间自动生成）(请在1min中内完成)");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            String str = scanner.nextLine(); //键盘获取输入的内容
            if(str.length()>0){
                socketChannel.write(Charset.forName("UTF-8").encode("客户端："+name+":"+str+"（已加载数据库）"));
                //System.out.println(Charset.forName("UTF-8").encode(name+":"+str));
            }

            //设计非堵塞模式
            socketChannel.configureBlocking(false);

            //设计buffer
        }

    }
    public static void main(String[] args) throws IOException, InterruptedException {

        String  strs [] = "qwer,".split(",");
        System.out.println(strs.length);

    }
}
