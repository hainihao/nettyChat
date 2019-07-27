package nettyGoup.canstant;

/**
 * @author:liguozheng
 * @Date:2019-05-15
 * @time:19:02
 * @description: 枚举类 标识用户消息类型状态
 */
public enum EnMsgType {

    /**用户登录消息*/
    EN_MSG_LOGIN,

    /**用户注册消息*/
    EN_MSG_REGISTER,

    /**用户忘记密码消息*/
    EN_MSG_FORGET_PWD,

    /**修改密码休息*/
    EN_MSG_MODIFY_PWD,

    /**一对一聊天消息*/
    EN_MSG_CHAT,

    /**群聊消息*/
    EN_MSG_CHAT_ALL,

    /**群发用户上线消息*/
    EN_MSG_NOTIFY_ONLINE,

    /**群发用户下线消息*/
    EN_MSG_NOTIFY_OFFLINE,

    /**用户下线消息*/
    EN_MSG_OFFLINE,
    /**获取所有在线用户信息*/
    EN_MSG_GET_ALL_USERS,

    /**传输文件消息*/
    EN_MSG_TRANSFER_FILE,

    /**用户是否存在【新增】*/
    EN_MSG_CHECK_USER_EXIST,

    /**是否存在离线消息【新增】*/
    EN_MSG_OFFLINE_MSG_EXIST,

    /**是否存在离线文件【新增】*/
    EN_MSG_OFFLINE_FILE_EXIST,

    /**响应消息*/
    EN_MSG_ACK

}
