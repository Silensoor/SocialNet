package socialnet.api.friends;

import lombok.Data;
import socialnet.dto.ComplexRs;
@Data
public class CommonRsComplexRs {

    private ComplexRs data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Long total;
}
