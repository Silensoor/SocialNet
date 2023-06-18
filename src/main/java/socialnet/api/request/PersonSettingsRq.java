package socialnet.api.request;

import lombok.Data;

@Data
public class PersonSettingsRq {
    private Boolean enable;
    private String notification_type;
}