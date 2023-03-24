package socialnet.dto.login;

import lombok.Data;
import socialnet.dto.PersonRs;

@Data
public class LoginRs {
    private PersonRs data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Integer timestamp;
    private Integer total;

}
