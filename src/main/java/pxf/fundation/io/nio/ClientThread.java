package pxf.fundation.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ClientThread implements Runnable {
    private Selector selector;
    ClientThread(Selector selector){
        this.selector=selector;
    }

    @Override
    public void run() {
//        while(true){

        for(;;){
            try {
                int length=selector.select();
                if(length ==0){
                    continue;
                }

                //        得到所有就绪状态的通道集合到key中
                Set<SelectionKey> selectionKeys = selector.selectedKeys(); //selectedKeys所有已经就绪的key集合

                //        转成集合迭代器
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while(iterator.hasNext()){

                    SelectionKey selectionKey = iterator.next();

                    if(selectionKey.isReadable()){
                        readOperator(selector,selectionKey);
                    }
                    iterator.remove();
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
    //    处理接收服务器信息事件
    private void readOperator(Selector selector, SelectionKey selectionKey) throws InterruptedException {
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

            //重新将channel注册到选择器上，设计为监听
            socketChannel.register(selector,SelectionKey.OP_READ);

        }catch (IOException e){
            selectionKey.cancel();
            System.out.println("服务器中断 开始准备重连");

            while (true){
                try {
                    new ChatClient().startClient("gx");

                } catch (IOException ioException) {
                    System.out.println("正在重连（5s） ");
                    //ioException.printStackTrace();
                    Thread.sleep(5000);
                    continue;
                }

            }
        }
    }

}
