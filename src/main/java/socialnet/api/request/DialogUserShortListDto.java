package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DialogUserShortListDto {
    private Long userId;

    @JsonProperty(value = "user_ids")
    private List<Long> userIds;
}
