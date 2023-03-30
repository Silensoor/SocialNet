package socialnet.dto;

import lombok.Data;

@Data
public class ErrorRs {
    private String error;
    private String errorDescription;
    private String timestamp;
}
