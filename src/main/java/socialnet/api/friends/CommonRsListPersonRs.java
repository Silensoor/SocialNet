package socialnet.api.friends;

import lombok.Data;
import socialnet.dto.PersonRs;

import java.util.List;
@Data
public class CommonRsListPersonRs {

    private List<PersonRs> data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Long timestamp;
    private Long total;
}
