package socialnet.dto;

import lombok.Data;

@Data
public class CommonRs<T> {
    private T data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Integer timestamp;
    private Integer total;
}
