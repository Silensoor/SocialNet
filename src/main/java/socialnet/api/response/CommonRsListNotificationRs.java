package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonRsListNotificationRs {
    List<NotificationRs> data;
    Integer itemPerPage;
    Integer offset;
    Integer perPage;
    @JsonProperty("timestamp")
    Integer timesTamp;
    Integer total;
}
