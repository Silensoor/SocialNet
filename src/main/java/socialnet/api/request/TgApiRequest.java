package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TgApiRequest {
    private long id;
    private String command;
    private String data;
}
