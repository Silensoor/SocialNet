package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DialogRs {

    @JsonProperty("author_id")
    private Long authorId;

    private Long id;

    @JsonProperty("last_message")
    private MessageRs lastMessage;

    @JsonProperty("read_status")
    private String readStatus;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("unread_count")
    private Long unreadCount;
}
