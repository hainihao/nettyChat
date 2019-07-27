package nettyGoup.bean;

/**
 * @author:liguozheng
 * @Date:2019-05-26
 * @time:00:38
 * @description:
 */


public class Recv {

    String fromname;
    String path;
    String dataTime;

    public Recv(String fromname, String path, String dataTime) {
        this.fromname = fromname;
        this.path = path;
        this.dataTime = dataTime;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
}
