package nettyGoup.file;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author:liguozheng
 * @Date:2019-05-25
 * @time:14:51
 * @description:  文件操作类
 */

public class FileSendHandler extends Thread{

    private String ip;
    private int port;
    private String toname;
    private File file;

    public FileSendHandler(String ip,int port,String toname,File file){

        this.ip = ip;
        this.port = port;
        this.toname = toname;
        this.file = file;
    }

    @Override
    public void run() {
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(ip,port));

            String name = file.getName();

            FileInputStream fileInputStream = new FileInputStream(file);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(name);
            byte[] b = new byte[1024];
            int len;

            while ((len = fileInputStream.read(b))!=(-1)){

                dataOutputStream.write(b,0,len);

            }

            fileInputStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();

                System.out.println("发送成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
