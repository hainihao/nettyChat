package nettyGoup.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import nettyGoup.controller.DispatcherContorller;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;

/**
 * @author:liguozheng
 * @Date:2019-05-11
 * @time:21:09
 * @description:  handler辅助类
 */

public class ServerHandler extends SimpleChannelInboundHandler {




    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {

        ByteBuf msg1 = (ByteBuf) msg;
        byte[] bytes = new byte[msg1.readableBytes()];

        msg1.readBytes(bytes);
        String msg2 = new String(bytes);

        DispatcherContorller controller = DispatcherContorller.getInstance();
        String recvMsg = controller.process(ctx, msg2);

        if (!"".equals(recvMsg)) {
            ctx.channel().writeAndFlush(recvMsg);
        }
    }

    /**
     * 用户上线处理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        SocketAddress socketAddress = channel.remoteAddress();
        System.out.println("客户端："+socketAddress+" 上线了");
    }

    /**
     * 用户下线处理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        SocketAddress socketAddress = channel.remoteAddress();
        System.out.println("客户端："+socketAddress+" 下线了");
        System.out.println();
    }
}
