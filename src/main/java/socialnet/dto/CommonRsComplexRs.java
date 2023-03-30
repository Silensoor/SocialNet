package socialnet.dto;

import lombok.Data;

@Data
public class CommonRsComplexRs {

    private ComplexRs data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Integer timestamp;
    private Integer total;
}
