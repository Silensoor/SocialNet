package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationRs {
    @JsonProperty("entity_author")
    private PersonRs entityAuthor;
    private Integer id;
    private String info;
    @JsonProperty("notification_type")
    private String notificationType;
    @JsonProperty("sent_time")
    private Date sentTime;
}
