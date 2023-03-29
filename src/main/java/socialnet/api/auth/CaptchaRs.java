package socialnet.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaRs {
    private String code;
    private String image;
}
