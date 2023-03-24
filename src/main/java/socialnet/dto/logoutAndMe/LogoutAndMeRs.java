package socialnet.dto.logoutAndMe;

import lombok.Data;
import socialnet.dto.ComplexRs;

@Data
public class LogoutAndMeRs {

    private ComplexRs data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Integer timestamp;
    private Integer total;
}
