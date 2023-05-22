package socialnet.api.request;

import lombok.Data;

@Data
public class EmailRq {
    private String email;
    private String secret;
}
