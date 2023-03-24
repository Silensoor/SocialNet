package socialnet.dto;

import lombok.Data;

@Data
public class LoginData {
    private String about;
    private String birthDate;
    private String country;
    private LoginCurrency currency;
}
