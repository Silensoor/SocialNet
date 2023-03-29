package socialnet.api.account;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RegisterRs {
    private String message;
    private String email;
    private Timestamp timestamp;
}
