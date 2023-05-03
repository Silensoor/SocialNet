package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TgApiRs {
    private String status;
    private String error;
    private String data;
}
