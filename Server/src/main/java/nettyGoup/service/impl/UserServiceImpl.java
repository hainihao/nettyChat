package nettyGoup.service.impl;

import nettyGoup.bean.Recv;
import nettyGoup.bean.User;
import nettyGoup.dao.UserDao;
import nettyGoup.dao.impl.UserDaoImpl;
import nettyGoup.service.UserService;

import java.util.ArrayList;

/**
 * @author:liguozheng
 * @Date:2019-05-15
 * @time:21:11
 * @description:
 */

public class UserServiceImpl implements UserService {

    private UserDao userDao = new UserDaoImpl();

    @Override
    public boolean doLogin(String name, String pwd) {
        boolean result = false;
        //调用dao层查询数据库
        User user = userDao.getUserByNameAndPwd(name, pwd);
        if (user != null) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean register(String name,String pwd) {

        return userDao.registerUser(name, pwd);
    }

    @Override
    public User forgetPassword(String name,String mail) {

        return userDao.checkAndGetMail(name, mail);
    }

    @Override
    public boolean changePwd(String name, String pwd, String newPwd) {

        return userDao.updatePassword(name,pwd,newPwd);
    }

    @Override
    public boolean check(String toname) {
        return userDao.checkToname(toname);
    }

    @Override
    public boolean offline_msg(String msgtype, String fromname, String toname, String msg) {
        return userDao.offline_msg(msgtype,fromname,toname,msg);
    }

    @Override
    public String getOfflineMsg(String name) {
        return userDao.getOfflineMsg(name);
    }

    @Override
    public boolean offLineFile(String fromname, String toname, String offLinePath) {

        return userDao.offLineFile(fromname,toname,offLinePath);
    }

    @Override
    public ArrayList<Recv> getOfflineFile(String name) {
        return userDao.getOfflineFile(name);
    }
}
