package socialnet.utils;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

@Component
public class Converter {
    public Long dateToMillisec(String dateStr
    ) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = simpleDateFormat.parse(dateStr);
        return date.getTime();
    }

    public Timestamp dateToTimeStamp(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (dateStr == null) return null;
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return new Timestamp(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
