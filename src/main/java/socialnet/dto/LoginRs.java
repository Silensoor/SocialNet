package socialnet.dto;

import lombok.Data;

@Data
public class LoginRs {

    private PersonRs data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Integer total;

}
