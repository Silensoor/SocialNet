package socialnet.dto;

import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class LoginRq {
    private String email;
    private String password;
}
