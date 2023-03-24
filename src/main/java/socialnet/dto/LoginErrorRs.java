package socialnet.dto;

import lombok.Data;

@Data
public class LoginErrorRs {
    private String error;
    private String errorDescription;
    private String timestamp;
}
