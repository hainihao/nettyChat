package nettyGoup.dao;

import nettyGoup.bean.Recv;
import nettyGoup.bean.User;

import java.util.ArrayList;

/**
 * @author:liguozheng
 * @Date:2019-05-15
 * @time:20:56
 * @description: 用户相关的dao接口层
 */

public interface UserDao {

    /**
     * 通过用户名和密码查询用户是否存在
     * @param name String
     * @param pwd String
     * @return
     */
    User getUserByNameAndPwd(String name,String pwd);

    boolean registerUser(String name,String ped);

    User checkAndGetMail(String name, String mail);

    boolean updatePassword(String name, String pwd, String nwePwd);

    boolean checkToname(String toname);

    boolean offline_msg(String msgtype, String fromname, String toname, String msg);

    String getOfflineMsg(String name);

    boolean offLineFile(String fromname, String toname, String offLinePath);

    ArrayList<Recv> getOfflineFile(String name);
}

