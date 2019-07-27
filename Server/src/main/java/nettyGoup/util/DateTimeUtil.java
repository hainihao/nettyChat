package nettyGoup.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author:liguozheng
 * @Date:2019-05-22
 * @time:20:51
 * @description:
 */

public class DateTimeUtil {

    public static String getDateTime(Date date) {

        String format = "yyyy年MM月dd日 HH:mm:ss";
        return new SimpleDateFormat(format).format(date);
    }
}
