package nettyGoup.service;

import nettyGoup.bean.Recv;
import nettyGoup.bean.User;

import java.util.ArrayList;

/**
 * @author:liguozheng
 * @Date:2019-05-15
 * @time:21:09
 * @description: 用户服务接口
 */

public interface UserService {

    /**
     * 登录操作
     * @param name
     * @param pwd
     * @return true : 用户存在
     *         false : 用户不存在
     */
    boolean doLogin(String name, String pwd);

    boolean register(String name,String pwd);

    User forgetPassword(String name, String mail);

    boolean changePwd(String name,String pwd, String newPwd);

    boolean check(String toname);

    boolean offline_msg(String msgtype, String fromname, String toname, String msg);

    String getOfflineMsg(String name);

    boolean offLineFile(String fromname, String toname, String offLinePath);

    ArrayList<Recv> getOfflineFile(String name);
}

