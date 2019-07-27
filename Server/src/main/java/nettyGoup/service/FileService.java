package nettyGoup.service;

import nettyGoup.bean.Recv;
import nettyGoup.util.PortUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author:liguozheng
 * @Date:2019-05-25
 * @time:15:49
 * @description:
 */

public class FileService extends Thread {

    private int fromPort;
    private int toPort;
    private int isOnline;
    private static String offLinePath = "/Users/liguozheng/file/offLineFile";
    private UserService userService;
    private String fromname = null;
    private String toname = null;
    private ArrayList<Recv> fileList;

    public FileService(int fromport,int toPort,int isOnline,UserService userService){

        this.fromPort = fromport;
        this.toPort = toPort;
        this.isOnline = isOnline;
        this.userService = userService;
    }

    @Override
    public void run() {

        if (isOnline==1){
            onlineTwo();
        }else if (isOnline==2){
            onlineOne();
        }else {
            recvFile();
        }
    }

    private void onlineTwo(){

        ServerSocket fromServerSocket = null;
        ServerSocket toServerSocket = null;
        Socket fromSocket = null;
        Socket toSocket = null;
        try {

            fromServerSocket = new ServerSocket(fromPort);
            toServerSocket = new ServerSocket(toPort);

            fromSocket = fromServerSocket.accept();
            toSocket = toServerSocket.accept();

            DataInputStream fromStream = new DataInputStream(fromSocket.getInputStream());
            DataOutputStream toStream = new DataOutputStream(toSocket.getOutputStream());

            String s = fromStream.readUTF();
            toStream.writeUTF(s);
            int lean ;
            byte[] b = new byte[1024];
            while ((lean = fromStream.read(b))!=-1){
                toStream.write(b,0,lean);
            }

            toStream.close();
            fromStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fromServerSocket!=null){
                try {
                    fromServerSocket.close();
                    fromSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (toServerSocket!=null){
                try {
                    toServerSocket.close();
                    toSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PortUtils.closePort(fromPort);
            PortUtils.closePort(toPort);
        }
    }

    private void onlineOne(){

        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(fromPort);
            socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String name = dataInputStream.readUTF();
            String filePath = offLinePath+ File.separator+name;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            int len;
            byte[] bytes = new byte[1024];

            while ((len = dataInputStream.read(bytes))!=-1){
                fileOutputStream.write(bytes,0,len);
            }

            fileOutputStream.close();

            dataInputStream.close();

            userService.offLineFile(fromname,toname,filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void recvFile(){

        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(toPort);
            socket = serverSocket.accept();

            StringBuilder stringBuilder = new StringBuilder();

            for (Recv recv:fileList){

                File file = new File(recv.getPath());
                String fromName = recv.getFromname();
                String dataTime = recv.getDataTime();
                String fileName = file.getName();

                stringBuilder.append(fromName+"±");
                stringBuilder.append(fileName+"±");
                stringBuilder.append(dataTime+"±");
                stringBuilder.append(file.length()+"±");
            }


            boolean a = true;

            FileInputStream fileInputStream = null;
            DataOutputStream dataOutputStream = null;

            for (Recv recv:fileList) {

                File file = new File(recv.getPath());

                if (a){
                    dataOutputStream  = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(stringBuilder.toString());
                    a = false;
                }

                 fileInputStream = new FileInputStream(file);

                byte[] b = new byte[1024];
                int len;

                while ((len = fileInputStream.read(b)) != (-1)) {

                    dataOutputStream.write(b, 0, len);
                }


                delete(recv.getPath());
            }
            fileInputStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            }
            else {
                return deleteDirectory(fileName);
            }
        }
    }

    private static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileService.deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileService.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public void setToname(String toname) {
        this.toname = toname;
    }

    public void setFileList(ArrayList<Recv> fileList) {
        this.fileList = fileList;
    }
}
