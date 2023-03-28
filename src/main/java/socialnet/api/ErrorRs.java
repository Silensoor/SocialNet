package socialnet.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorRs {
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
    private Timestamp timestamp;
}
