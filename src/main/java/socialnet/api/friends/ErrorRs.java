package socialnet.api.friends;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ErrorRs {
    private String error;
    private String error_description;
    private Long timestamp;
}
