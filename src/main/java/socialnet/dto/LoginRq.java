package socialnet.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class LoginRq {
    @NotNull
    private String email = "dsada";
    @NotNull
    private String password = "dsadas";
}
