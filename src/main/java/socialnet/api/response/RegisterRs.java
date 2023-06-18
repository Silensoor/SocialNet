package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRs {
    private ComplexRs data;
    private String email;
    private Long timestamp;

    public RegisterRs(String email, Long timestamp) {
        this.data = new ComplexRs();
        this.email = email;
        this.timestamp = timestamp;
    }

}
