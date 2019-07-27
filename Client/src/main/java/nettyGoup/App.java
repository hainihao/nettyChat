package nettyGoup;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import nettyGoup.netty.ClientHandler;
import nettyGoup.netty.ClientSendHander;

/**
 * 客户端启动类
 *1326498135@qq.com
 * @author liguozheng
 */
public class App {
    public static void main( String[] args ) {

        start("127.0.0.1",7779);

    }

    public static void start(String host,int port) {

        NioEventLoopGroup loopGroup = new NioEventLoopGroup();


        //启动辅助类
        Bootstrap bootstrap = new Bootstrap()
                .group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel){

                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new ClientHandler());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
                    }
                });

        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("客户端连接上服务器");

            //客户端发送消息
            ClientSendHander sendHandler = new ClientSendHander(channel);
            sendHandler.sendMsg();


            //同步关闭客户端
           channel.closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            loopGroup.shutdownGracefully();
        }
    }
}
