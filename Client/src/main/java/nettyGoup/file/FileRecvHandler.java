package nettyGoup.file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author:liguozheng
 * @Date:2019-05-25
 * @time:16:09
 * @description:
 */

public class FileRecvHandler extends Thread {

    private int port;

    public FileRecvHandler(int port){
        this.port = port;
    }

    @Override
    public void run() {
        recv();
//        Socket socket = new Socket();
//
//        try {
//            socket.connect(new InetSocketAddress("127.0.0.1", port));
//
//            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
//
//            String name = dataInputStream.readUTF();
//            System.out.println(name);
//            String filePath = getDeafultPath()+ File.separator+"c.java";
//            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
//
//            int len;
//            byte[] bytes = new byte[1024];
//
//            while ((len = dataInputStream.read(bytes))!=-1){
//                fileOutputStream.write(bytes,0,len);
//            }
//
//
//            fileOutputStream.close();
//
//            dataInputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                socket.close();
//                System.out.println("接收成功");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


    public void recv(){

        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress("127.0.0.1", port));

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            String name = dataInputStream.readUTF();
            String[] names = name.split("±");

            for (int i = 0;i+4<=names.length;i=i+4) {

                System.out.print(names[i]+"  发送：");
                System.out.print(names[i+1]);
                String filePath = getDeafultPath() + File.separator + names[i+1];
                Long l = Long.valueOf(names[i + 3]);
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                int len;
                byte[] bytes = new byte[1024];

                while (l>=0){

                    if (l-1024>=0){
                        len = dataInputStream.read(bytes);
                        fileOutputStream.write(bytes,0,len);
                        l-=1024;
                        fileOutputStream.flush();
                    }else {

                        byte[] bytes1 = new byte[Math.toIntExact(l)];
                        len = dataInputStream.read(bytes1);
                        fileOutputStream.write(bytes1,0,len);
                        fileOutputStream.flush();

                        System.out.print("   时间："+names[i+2]);
                        System.out.println();
                        break;
                    }

                }

                fileOutputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
                System.out.println("接收成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private String getDeafultPath(){
        File file = new File("");
        String path = file.getAbsolutePath();
        return path;
    }
}
