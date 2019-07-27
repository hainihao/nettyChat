package nettyGoup.netty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import nettyGoup.canstant.EnMsgType;
import nettyGoup.file.FileRecvHandler;
import nettyGoup.util.JsonUtils;

import java.util.concurrent.SynchronousQueue;

/**
 * @author:liguozheng
 * @Date:2019-05-11
 * @time:21:13
 * @description:  sendfile:2:/Users/liguozheng/file/BSTTest.java
 */

public class ClientHandler extends SimpleChannelInboundHandler<String> {

   public static SynchronousQueue<Integer> sQueue =  new SynchronousQueue();



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf msg1 = (ByteBuf) msg;
        byte[] bytes = new byte[msg1.readableBytes()];

        msg1.readBytes(bytes);
        String msg2 = new String(bytes);

        //将服务端返回数据转换为JSON格式
        ObjectNode jsonNodes = JsonUtils.getObjectNode(msg2);

        String msgtype = jsonNodes.get("msgtype").asText();

        int code = 0;
        if (String.valueOf(EnMsgType.EN_MSG_ACK).equals(msgtype)) {
            //ack消息
            String srctype = jsonNodes.get("srctype").asText();
            if (String.valueOf(EnMsgType.EN_MSG_LOGIN).equals(srctype)) {
                //登录操作返回的结构
                code = jsonNodes.get("code").asInt();

                //将登录返回结构给给主线程处理

            }else if (String.valueOf(EnMsgType.EN_MSG_REGISTER).equals(srctype)){
                code = jsonNodes.get("code").asInt();

                //将登录返回结构给给主线程处理
            }else if (String.valueOf(EnMsgType.EN_MSG_FORGET_PWD).equals(srctype)){
                code = jsonNodes.get("code").asInt();
            }else if (String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD).equals(srctype)){
                code = jsonNodes.get("code").asInt();
            }else if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(srctype)){
                code = jsonNodes.get("code").asInt();
            }else if (String.valueOf(EnMsgType.EN_MSG_OFFLINE).equals(srctype)){
                String s = jsonNodes.get("offlineMsg").asText();
                System.out.println(s);
            }else if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(srctype)){

            }else if (String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE).equals(srctype)){
                sQueue.put(jsonNodes.get("port").asInt());
            }
            sQueue.put(code);
        }else if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(msgtype)){
            String namel = jsonNodes.get("fromname").asText();
            String msgl = jsonNodes.get("msg").asText();
            System.out.println(namel+": "+msgl);
        }else if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(msgtype)){
            String namel = jsonNodes.get("fromname").asText();
            String msgl = jsonNodes.get("msg").asText();
            System.out.println(namel+": "+msgl);
        }else if (String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE).equals(msgtype)){

            int port = jsonNodes.get("port").asInt();
            new FileRecvHandler(port).start();

        }
    }
}
