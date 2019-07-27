package nettyGoup.netty;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import nettyGoup.canstant.EnMsgType;
import nettyGoup.file.FileSendHandler;
import nettyGoup.util.JsonUtils;

import java.io.File;
import java.util.Scanner;

/**
 * @author:liguozheng
 * @Date:2019-05-15
 * @time:19:09
 * @description:
 */

public class ClientSendHander {

    /**
     * 和服务端头通信的Channel实例
     */
    private Channel channel;


    /**
     * 接受键盘输入
     */
    private Scanner scanner;

    //当前人名字
    static String s;

    /**
     * 构造方法，拿到channel通道
     *
     * @param channel
     */
    public ClientSendHander(Channel channel) {

        this.channel = channel;
        this.scanner = new Scanner(System.in);
        this.scanner.useDelimiter("\n");
    }


    /**
     * 客户端发送消息
     */
    public void sendMsg() {

        while (true) {

            showLoginView();
            System.out.println("请选择操作：");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    /**登录*/
                    doLogin();
                    break;
                case 2:
                    /**注册*/
                    register();
                    break;
                case 3:
                    /**忘记密码*/
                    forgetPassword();
                    break;
                case 4:
                    /**退出系统*/
                    System.exit(0);
                default:
                    System.out.println("输入不合法，请重新输入：");
            }
        }
    }

    private void forgetPassword() {
        System.out.println("请输入用户名：");
        String name = scanner.nextLine();
        System.out.println("请输入邮箱(qq)：");
        String mail = scanner.nextLine();

        ObjectNode node = JsonUtils.getObjectNode();
        node.put("msgtype", String.valueOf(EnMsgType.EN_MSG_FORGET_PWD));
        node.put("name", name);
        node.put("mail", mail);
        String s = node.toString();
        channel.writeAndFlush(s);
        printRecives();

    }

    private void register() {

        System.out.println("请输入用户名：");
        String p = scanner.nextLine();
        System.out.println("请输入密码：");
        String w = scanner.nextLine();

        ObjectNode node = JsonUtils.getObjectNode();
        node.put("msgtype", String.valueOf(EnMsgType.EN_MSG_REGISTER));
        node.put("name", p);
        node.put("pwd", w);
        String s = node.toString();
        channel.writeAndFlush(s);

       printRecives();

    }

    private void showLoginView() {
        System.out.println("-----------------------");
        System.out.println("1.登录");
        System.out.println("2.注册");
        System.out.println("3.忘记密码");
        System.out.println("4.退出系统");
        System.out.println("-----------------------");

    }

    /**
     * 登录
     */
    private void doLogin() {
        //接收用户输入
        System.out.println("请输入用户名：");
        String name = scanner.nextLine();
        System.out.println("请输入密码：");
        String pwd = scanner.nextLine();
        System.out.println("登录信息：name：" + name + ",pwd:" + pwd);

        //封装消息
        ObjectNode node = JsonUtils.getObjectNode();
        node.put("msgtype", String.valueOf(EnMsgType.EN_MSG_LOGIN));
        node.put("name", name);
        node.put("pwd", pwd);
        String sendMsg = node.toString();

        //发送给服务端
        channel.writeAndFlush(sendMsg);

        s = name;
         printRecives();

    }



    private void changePassword() {

        System.out.println("请输入用户名：");
        String name = scanner.nextLine();
        System.out.println("请输入旧密码：");
        String oldPwd = scanner.nextLine();
        System.out.println("请输入新密码：");
        String p1 = scanner.nextLine();
        System.out.println("请再次输入：");
        String p2 = scanner.nextLine();

        if (!p1.equals(p2)){
            System.out.println("两次输入不对，请重新输入！");
        }else {

            ObjectNode node = JsonUtils.getObjectNode();
            node.put("msgtype", String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD));
            node.put("name",name);
            node.put("pwd", oldPwd);
            node.put("newPwd", p1);
            String sendMsg = node.toString();

            //发送给服务端
            channel.writeAndFlush(sendMsg);

        }

        printRecives();
    }


    /**
     * 主菜单界面
     */
    private void showMemoView(){
        System.out.println("====================系统使用说明====================");
        System.out.println("                         注：输入多个信息用\":\"分割");
        System.out.println("1.输入modifypwd:username 表示该用户要修改密码");
        System.out.println("2.输入getallusers 表示用户要查询所有人员信息");
        System.out.println("3.输入to:xxx 表示一对一聊天");
        System.out.println("4.输入all:xxx 表示发送群聊消息");
        System.out.println("5.输入sendfile:xxx 表示发送文件请求:[sendfile][接收方用户名][发送文件路径]");
        System.out.println("6.输入quit 表示该用户下线，注销当前用户重新登录");
        System.out.println("7.输入help查看系统菜单");
        System.out.println("8.输入getChat查看离线信息");
        System.out.println("9.输入getFile查看离线信息");
        System.out.println("================================================");

    }

    private void menuChess() {




        String msg = scanner.nextLine();

        switch (msg) {

            case "modifyped":

                //修改密码
                changePassword();
                break;
            case "getallusers":

                //查询所有人的信息

                break;
            case "sendfile":

                //表示发送文件请求

                break;
            case "quit":

                //表示用户下线

                break;
            case "help":

                //查看帮助

                break;
            case "getChat":

                //查看离线信息
                getOfflineMsg();
                break;
            case "getFile":

                //查看离线信息
                getOfflineFile();
                break;
            default:
                String[] split = msg.split(":");
                String toname = split[0];
                String msgl = split[1];

                if (toname.equals("all")){

                    toall(msgl);

                }else if (toname.equals("sendfile")){
                    sendFile(msgl,split[2]);
                }else {
                    toUser(toname,msgl);
                }


        }

    }

    private void getOfflineFile() {

        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_OFFLINE_FILE_EXIST));
        objectNode.put("name",s);
        channel.writeAndFlush(objectNode.toString());
    }

    /**
     * 发送文件
     * @param toname 给谁发
     * @param filePath  文件路径
     *                  sendfile:1:/Users/liguozheng/file/BSTTest.java
     */
    private void sendFile(String toname,String filePath) {

        File file = new File(filePath);
        if (!(file.exists()&&file.isFile())){
            System.out.println("文件路径不合法");
            return;
        }
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_TRANSFER_FILE));
        objectNode.put("fromname", s);
        objectNode.put("toname", toname);
        channel.writeAndFlush(objectNode.toString());

        int prot = 0;

        try {
            prot = ClientHandler.sQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (prot>0){

             new FileSendHandler("127.0.0.1", prot, toname, file).start();
        }
    }

    /**
     * 给所有人发消息
     * @param msgl
     */
    private void toall(String msgl) {

        ObjectNode objectNode = JsonUtils.getObjectNode();

        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL));
        objectNode.put("fromname",s);
        objectNode.put("msg",msgl);

        channel.writeAndFlush(objectNode.toString());


    }

    private void getOfflineMsg() {
        ObjectNode objectNode = JsonUtils.getObjectNode();

        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_OFFLINE));
        objectNode.put("name",s);

        channel.writeAndFlush(objectNode.toString());
    }

    private void toUser(String toname,String msg) {



        ObjectNode objectNode = JsonUtils.getObjectNode();

        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT));
        objectNode.put("fromname",s);
        objectNode.put("toname",toname);
        objectNode.put("msg",msg);

        channel.writeAndFlush(objectNode.toString());

        printRecives();
    }


    private int printRecives(){

        int code = 0;
        try {
            code = (int) ClientHandler.sQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (code){
            //成功：展示菜单页面 while(true)
            case 200 :

                System.out.println("登陆成功");
                showMemoView();
                System.out.println("请选择操作：");
                while (true) {
                    menuChess();
                }
            case 300 :
            //失败：
            System.out.println("登录失败 ：" + code);
                return 300;

            case 400 :

                System.out.println("注册成功");
                return 400;

            case 500 :

                System.out.println("注册失败 ：" + code);
                return 500;

            case 600 :

                System.out.println("请查看电子邮箱 ：" + code);
                return 600;

            case 700 :

                System.out.println("密码已经发送到邮箱请查收。");
                return 700;

            case 800 :

                System.out.println("密码修改成功。");
                return 800;

            case 900 :

                System.out.println("密码修改失败。");
                return 900;
            case 101 :

                System.out.println("发送成功。");
                return 101;
            case 102 :

                System.out.println("用户不在线。");
                return 102;

            case 103 :

                System.out.println("用户不存在。");
                return 103;
            default:
                return 0;
        }

    }
}
