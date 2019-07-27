package nettyGoup.dao.impl;

import nettyGoup.bean.Recv;
import nettyGoup.bean.User;
import nettyGoup.dao.UserDao;
import nettyGoup.util.C3p0Util;
import nettyGoup.util.DateTimeUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author:liguozheng
 * @Date:2019-05-15
 * @time:21:01
 * @description:
 */

public class UserDaoImpl implements UserDao {

    @Override
    public User getUserByNameAndPwd(String name, String pwd) {

        return check(name,pwd,"pwd");
    }

    @Override
    public boolean registerUser(String name, String pwd) {

        boolean execute = true;
        Connection connection = C3p0Util.getConnection();
        PreparedStatement statement;

        int id = getMaxIndex(connection,"user") +1;


        String sql = "insert into user values(?,?,?,'scac')";
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, pwd);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return execute;
    }

    @Override
    public User checkAndGetMail(String name, String mail) {

        return check(name,mail,"email");
    }

    @Override
    public boolean updatePassword(String name, String pwd, String nwePwd) {

        User user = getUserByNameAndPwd(name,pwd);

        if (user==null){
            return false;
        }


        String sql = "update user set pwd = ? where id = ?";

        Connection connection = C3p0Util.getConnection();

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,nwePwd);
            statement.setInt(2, user.getId());

            int i = statement.executeUpdate();

              return i>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkToname(String toname) {

        Connection connection = C3p0Util.getConnection();

        String sql = "select * from user where name=?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, toname);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean offline_msg(String msgtype, String fromname, String toname, String msg) {

        Connection connection = C3p0Util.getConnection();

        int id = getMaxIndex(connection,"offline_msg")+1;
        String sql = "insert into offline_msg values(?,?,?,?,?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, toname);
            statement.setString(3, fromname);
            statement.setString(4, msg);
            statement.setString(5, DateTimeUtil.getDateTime(new Date()));

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public String getOfflineMsg(String name) {

        Connection connection = C3p0Util.getConnection();

        String sql1 = "select * from offline_msg where to_name=?";
        String sql2 = "delete from offline_msg where to_name=?";

        StringBuilder offlineMsg = new StringBuilder();
        try {
            PreparedStatement statement = connection.prepareStatement(sql1);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                offlineMsg.append(resultSet.getString("from_name")+" : ");

                offlineMsg.append(resultSet.getString("msg"));
                offlineMsg.append("datetime");
                offlineMsg.append("\n");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1,name);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offlineMsg.toString();
    }

    @Override
    public boolean offLineFile(String fromname, String toname, String offLinePath) {

        Connection connection = C3p0Util.getConnection();

        int id = getMaxIndex(connection, "offline_file")+1;

        String sql = "insert into offline_file values(?,?,?,?,?)";

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, fromname);
            statement.setString(3, toname);
            statement.setString(4, offLinePath);
            statement.setString(5, DateTimeUtil.getDateTime(new Date()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public ArrayList<Recv> getOfflineFile(String name) {

        Connection connection = C3p0Util.getConnection();

        String sql = "select * from offline_file where toname = ?";
        String sql2 = "delete from offline_file where toname=?";

        ArrayList<Recv> paths = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String path = resultSet.getString("path");
                String fromname = resultSet.getString("fromname");
                String dataTime = resultSet.getString("dataTime");
                paths.add(new Recv(fromname,path,dataTime));
            }
            statement = connection.prepareStatement(sql2);
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return paths;
    }

    public User check(String p1, String p2,String check) {
        User user = null;

        Connection connection = C3p0Util.getConnection();

        String sql = "select * from user where name=? and "+check+"=?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, p1);
            statement.setString(2, p2);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPwd(resultSet.getString("pwd"));
                user.setEmail(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    private int getMaxIndex(Connection connection,String table){
        String sql = "select * from "+table +" where id =(select max(id) from "+table+")";
        PreparedStatement statement = null;
        int id = 0;
        try {
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = Integer.valueOf(resultSet.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }
}

