package pxf.fundation.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

public class ChatServe {
    public void StratServer(int port) throws IOException, IOException {
        //soket通道 客户通道
        //创建服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //非堵塞模式
        serverSocketChannel.configureBlocking(false);

        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(port));

        //创建selector 选择器
        Selector selector = Selector.open();

        //注册通道
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //轮播查询
        System.out.println("智能水表服务端(端口:"+port+")已经启动");

//        如果有就绪状态的通道 则select方法返回1
        while(true){

            while (selector.select()>0){
//            因为有多个通道 ，所以采用集合 获取所有就绪的通道
                //        得到所有就绪状态的通道集合到key中
                Set<SelectionKey> selectionKeys = selector.selectedKeys(); //selectedKeys所有已经就绪的key集合

//        转成集合迭代器
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while(iterator.hasNext()){

                    SelectionKey selectionKey = iterator.next();

                    //有人来连
                    if(selectionKey.isAcceptable()){
                        acceptOperator(serverSocketChannel,selector);
                    }
                    //发过来了已经
                    else if(selectionKey.isReadable()){
                        readOperator(selector,selectionKey);
                    }
                    //返回水表数据
                    else if(selectionKey.isWritable()){
                        writeOperator(selector,selectionKey);
                    }
                    iterator.remove();
                }
            }

        }
    }

    //处理服务器写事件
    private void writeOperator(Selector selector,SelectionKey selectionKey) {
        try {
            //有channel可写,取出可写的channel
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            //                    设计非阻塞
            socketChannel.configureBlocking(false);

            socketChannel.write(Charset.forName("UTF-8").encode("数据库存入成功！" ));
            //重新将channel注册到选择器上，设计为监听
            socketChannel.register(selector,SelectionKey.OP_READ);
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    //    处理读事件
    private void readOperator(Selector selector, SelectionKey selectionKey) throws IOException {

        try {
            //                    获取就绪通道
            SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
//                    设计buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);

//                    循环读客户端数据
            int length=0;
            String msg="";
            if((length=socketChannel.read(buffer))>0){  //读到buffer里面
//                        切换模式
                buffer.flip();
                msg+= Charset.forName("UTF-8").decode(buffer);  //从buffer里面取数据 解码
            }
            System.out.println(msg);

            String str[]=msg.split(":");

            String temp=str[1];

            String str2[]=temp.split("、");
            SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = tempDate.format(new java.util.Date());
            System.out.println(datetime);
           // JdbcUtil.find(new User(str2[0],str2[1],str2[2],datetime));

            //重新将channel注册到选择器上，设计为监听
            socketChannel.register(selector,SelectionKey.OP_WRITE);
        }catch (IOException e){
            selectionKey.cancel();
            selectionKey.channel().close();
            System.out.println("有客户端断连，我已主动关闭");
        }


        //光广播到其他用户上去
//                        if(msg.length()>0){
//                            System.out.println(msg);
//                            castOtherClient(msg,selector,socketChannel);
//                        }
    }

    //广播到其他客户端
//    private void castOtherClient(String msg, Selector selector, SocketChannel socketChannel) throws IOException {
//
//        //获取所有就绪的channel
//        Set<SelectionKey> selectionKeySet = selector.keys();
//
//    循环处理搜索就绪的channel
//        for (SelectionKey selectionKey : selectionKeySet){
//            获取每一个channel
//            Channel tarChannel = selectionKey.channel();
//
//   不给自己发信息
//            if(tarChannel instanceof SocketChannel && tarChannel!=socketChannel){
//                ((SocketChannel)tarChannel).write(Charset.forName("UTF-8").encode(msg)); //传输数据是编码，发送数据是解码
//            }
//        }
//    }

    //处理接收状态的通道
    private void acceptOperator(ServerSocketChannel serverSocketChannel, Selector selector)  {
        try {
            //                    获取连接
            SocketChannel socketChannel = serverSocketChannel.accept();
//                    设计非阻塞
            socketChannel.configureBlocking(false);
//                     注册通道
            socketChannel.register(selector,SelectionKey.OP_READ);

            //回复客户端消息
            socketChannel.write(Charset.forName("UTF-8").encode("您已成功连接到服务器！"));
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        new ChatServe().StratServer(7890);
    }
}
