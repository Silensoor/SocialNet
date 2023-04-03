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
}
