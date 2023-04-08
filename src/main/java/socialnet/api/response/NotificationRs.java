package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationRs {
    @JsonProperty("entity_author")
    PersonRs entityAuthor;
    Integer id;
    String info;
    @JsonProperty("notification_type")
    String notificationType;
    @JsonProperty("sent_time")
    Date sentTime;
}
