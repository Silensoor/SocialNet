package socialnet.model.db;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Captcha {
    private long id;
    private String code;
    private String secretCode;
    private Timestamp time;
}
