package socialnet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonRs<T> {
    private T data;
    private Integer itemPerPage;
    private Integer offset;
    private Integer perPage;
    private Integer timestamp;
    private Integer total;

    public CommonRs(T data, Integer timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }
}
