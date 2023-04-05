package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class CommonRs<T> {
    private T data;

    private Integer itemPerPage;

    private Integer offset;

    private Integer perPage;

    private Long timestamp;

    private Long total;

    public CommonRs(T data, Long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public CommonRs(T data) {
        this.data = data;
        this.itemPerPage = 20;
        this.offset = 0;
        this.perPage = 20;
        this.timestamp = System.currentTimeMillis();
        this.total = 0L;
    }

}
