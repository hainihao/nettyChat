package nettyGoup.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import nettyGoup.bean.Recv;
import nettyGoup.bean.User;
import nettyGoup.canstant.EnMsgType;
import nettyGoup.service.FileService;
import nettyGoup.service.UserService;
import nettyGoup.service.impl.UserServiceImpl;
import nettyGoup.util.JsonUtils;
import nettyGoup.util.PortUtils;
import nettyGoup.util.emailUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author:liguozheng
 * @Date:2019-05-15
 * @time:19:38
 * @description: 接收客户端通信并分发操作
 */

public class DispatcherContorller {

    private static DispatcherContorller contorller = new DispatcherContorller();

    private UserService userService = new UserServiceImpl();

    static HashMap<String , Channel> channelList = new HashMap<>();

    /**
     * 通过该方法获取实例
     * @return
     */
    public static DispatcherContorller getInstance(){

        return contorller;
    }

    /**
     * 服务端主要业务处理核心方法
     * @param ctx
     * @param msg
     * @return
     */
    public String process(ChannelHandlerContext ctx,String msg){

        ObjectNode objectNode = JsonUtils.getObjectNode(msg);

        String msgtype = objectNode.get("msgtype").asText();

        if (String.valueOf(EnMsgType.EN_MSG_LOGIN).equals(msgtype)) {

           return login(objectNode,ctx);
        }else if (String.valueOf(EnMsgType.EN_MSG_REGISTER).equals(msgtype)){

            return register(objectNode);
        }else if (String.valueOf(EnMsgType.EN_MSG_FORGET_PWD).equals(msgtype)){

            return forgetPaaaword(objectNode);
        }else if (String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD).equals(msgtype)){

            return changePwd(objectNode);
        }else if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(msgtype)){
            return changMsg(objectNode);
        }else if (String.valueOf(EnMsgType.EN_MSG_OFFLINE).equals(msgtype)){
            return getOfflineMsg(objectNode);
        }else if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(msgtype)){
            return toall(objectNode);
        }else if (String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE).equals(msgtype)){
            return sendFile(objectNode);
        }else if (String.valueOf(EnMsgType.EN_MSG_OFFLINE_FILE_EXIST).equals(msgtype)){
            return getOfflineFile(objectNode);
        }

        return "";
    }

    private String getOfflineFile(ObjectNode objectNode) {

        String name = objectNode.get("name").asText();
        ArrayList<Recv> offlineFileList = userService.getOfflineFile(name);
        int port = PortUtils.getFreePort();
        ObjectNode objectNode1 = JsonUtils.getObjectNode();

        objectNode1.put("msgtype",String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE));
        objectNode1.put("port",port);

        FileService fileService = new FileService(0, port, 3, null);
        fileService.setFileList(offlineFileList);
        fileService.start();

        return objectNode1.toString();
    }


    private String sendFile(ObjectNode objectNode) {
        String fromname = objectNode.get("fromname").asText();
        String toname = objectNode.get("toname").asText();

        Channel channel = channelList.get(toname);

        ObjectNode objectNode1 = JsonUtils.getObjectNode();
        objectNode1.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode1.put("srctype", String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE));



        if(userService.check(toname)){

            int fromPort = PortUtils.getFreePort();
            int toPort = 0;
            int isOnline = 2;

            if (channel!=null){

                isOnline = 1;

                toPort = PortUtils.getFreePort();

                ObjectNode objectNode2 = JsonUtils.getObjectNode();
                objectNode2.put("msgtype", String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE));
                objectNode2.put("port",toPort);
                channel.writeAndFlush(objectNode2.toString());

                objectNode1.put("code",101);


            }else {
                objectNode1.put("code",102);

            }

            objectNode1.put("port",fromPort);
            FileService fileService = new FileService(fromPort, toPort, isOnline, userService);

            fileService.setFromname(fromname);
            fileService.setToname(toname);

            fileService.start();

        }else {

            objectNode1.put("code",103);
        }
        return objectNode1.toString();
    }

    private String toall(ObjectNode objectNode) {

        ObjectNode objectNode1 = JsonUtils.getObjectNode();
        objectNode1.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode1.put("srctype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL));
        objectNode1.put("code",201);

        Iterator<Map.Entry<String, Channel>> iterator = channelList.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String, Channel> next = iterator.next();
            Channel value = next.getValue();
            value.writeAndFlush(objectNode.toString());
        }

        return objectNode1.toString();

    }

    private String getOfflineMsg(ObjectNode objectNode) {

        String name = objectNode.get("name").asText();

        String OfflineMsg = userService.getOfflineMsg(name);

        ObjectNode objectNode1 = JsonUtils.getObjectNode();
        objectNode1.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode1.put("srctype", String.valueOf(EnMsgType.EN_MSG_OFFLINE));
        objectNode1.put("offlineMsg",OfflineMsg);

        return objectNode1.toString();
    }

    private String changMsg(ObjectNode objectNode) {


        String toname = objectNode.get("toname").asText();

        Channel channel = channelList.get(toname);

        System.out.println(objectNode.toString());
        ObjectNode objectNode1 = JsonUtils.getObjectNode();
        objectNode1.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode1.put("srctype", String.valueOf(EnMsgType.EN_MSG_CHAT));

        if(userService.check(toname)){

            if (channel != null) {
                channel.writeAndFlush(objectNode.toString());
                objectNode1.put("code", 101);
            } else {
                offline_msg(objectNode);
                objectNode1.put("code", 102);
            }
        }else {

            objectNode1.put("code", 103);

        }
        return objectNode1.toString();
    }

    private void offline_msg(ObjectNode objectNode) {

        String msgtype = objectNode.get("msgtype").asText();
        String fromname = objectNode.get("fromname").asText();
        String toname = objectNode.get("toname").asText();
        String msg = objectNode.get("msg").asText();

        userService.offline_msg(msgtype,fromname,toname,msg);
    }


    /**
     * 修改密码请求
     * @param objectNode objectNode
     * @return String
     */
    private String changePwd(ObjectNode objectNode) {

        String name = objectNode.get("name").asText();
        String pwd = objectNode.get("pwd").asText();
        String newPwd = objectNode.get("newPwd").asText();

        boolean key = userService.changePwd(name, pwd, newPwd);
        ObjectNode recvNode = JsonUtils.getObjectNode();
        recvNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        recvNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD));

        if (key){
            //修改成功
            recvNode.put("code", 800);
        }else {
            //修改失败
            recvNode.put("code", 900);
        }

        return recvNode.toString();
    }


    /**
     * 忘记密码
     * @param objectNode ObjectNode
     * @return String
     */
    private String forgetPaaaword(ObjectNode objectNode) {

        String name = objectNode.get("name").asText();
        String mail = objectNode.get("mail").asText();

        User user = userService.forgetPassword(name, mail);

        ObjectNode recvNode = JsonUtils.getObjectNode();
        recvNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        recvNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_FORGET_PWD));

        if (user == null ){
            //600标识申请失败
            recvNode.put("code", 600);
        }else {
            //700标识申请成功
            recvNode.put("code", 700);
            emailUtil emailUtil = new emailUtil(user.getEmail());

            try {
                emailUtil.receiveMail("注意注意，环境异常，请注意！！！！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return recvNode.toString();
    }

    /**
     * 注册请求
     * @param objectNode ObjectNode
     * @return String
     */
    private String register(ObjectNode objectNode) {

        String name = objectNode.get("name").asText();
        String pwd = objectNode.get("pwd").asText();

        boolean register =  userService.register(name,pwd);

        ObjectNode recvNode = JsonUtils.getObjectNode();
        recvNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        recvNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_REGISTER));

        if (register){
            //400标识注册成功
            recvNode.put("code", 400);
        }else {
            //500标识注册失败
            recvNode.put("code", 500);
        }

        return recvNode.toString();
    }

    /**
     * 登录请求
     * @param objectNode  objectNode
     * @return String
     */
    private String login(ObjectNode objectNode,ChannelHandlerContext ctx){
        //登录操作
        String name = objectNode.get("name").asText();
        String pwd = objectNode.get("pwd").asText();

        //查询数据库操作
        boolean login = userService.doLogin(name, pwd);

        ObjectNode recvNode = JsonUtils.getObjectNode();
        recvNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        recvNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_LOGIN));
        if (login) {

            //200 表示登录成功
            recvNode.put("code", 200);
            channelList.put(name,ctx.channel());

        } else {
            //登录失败
            recvNode.put("code", 300);
        }

        return recvNode.toString();
    }
}
