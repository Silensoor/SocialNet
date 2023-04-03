package socialnet.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRq {
    @NotBlank(message = "Field 'code' is empty")
    private String code;

    @NotBlank(message = "Field 'code secret' is empty")
    private String codeSecret;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Field 'email' is empty")
    private String email;

    @NotBlank(message = "Field 'first name' is empty")
    private String firstName;

    @NotBlank(message = "Field 'last name' is empty")
    private String lastName;

    @NotBlank(message = "Field 'password' is empty")
    private String passwd1;

    @NotBlank(message = "Field 'password confirm' is empty")
    private String passwd2;
}
