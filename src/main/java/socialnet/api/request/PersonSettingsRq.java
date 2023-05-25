package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PersonSettingsRq {
    private Boolean enable;

    @JsonProperty("notification_type")
    private String notificationType;
}