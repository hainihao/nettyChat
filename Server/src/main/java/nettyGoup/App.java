package nettyGoup;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import nettyGoup.netty.ServerHandler;

/**
 * 服务端启动类
 * @author liguozheng
 */
public class App 
{
    public static void main( String[] args ) {

        start();
    }

    public static void start() {

        /*
          创建两个事件循环组NioEventLoopGroup
          第一个EventLoopGroup的作用接受客户端连接(accept)，将接收到的通道(Channel)分配给工作线程处理
          第二个EventLoopGroup的作用是将接收上一个EventLoopGroup提交的任务
          NioEventLoopGroup实际上是一个线程池死循环for(;;)
          事件循环实质是一个for死循环处理for(;;)
         */

        NioEventLoopGroup boss = new NioEventLoopGroup(5);
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            //ServerBootstrap服务端启动类，设置启动相关配置
            ServerBootstrap bootstrap = new ServerBootstrap()
                    //将主线程和工作线程传出对应线程组
                    .group(boss, worker)
                    //设置主线程处理的通道实例类
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("handler",new ServerHandler());
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                        }
                    });

            //同步方式启动服务端
            ChannelFuture sync = bootstrap.bind(7779).sync();
            //同步阻塞关闭
            sync.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
