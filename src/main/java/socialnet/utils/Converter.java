package socialnet.utils;

import lombok.experimental.UtilityClass;
import lombok.var;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

@Component
public class Converter {
    private final String ISO_DATE_FORMAT_ZERO_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private final String UTC_TIMEZONE_NAME = "UTC";

    public Long dateToMillisec(String dateStr
    ) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = simpleDateFormat.parse(dateStr);
        return date.getTime();
    }

    public Timestamp dateToTimeStamp(String dateStr) {
        dateStr = dateStr.substring(0,19).replace("T"," ");
        return Timestamp.valueOf(dateStr);
    }

}
