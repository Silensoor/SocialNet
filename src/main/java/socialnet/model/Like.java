package socialnet.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Like {
    private Long id;
    private String type;
    private Long entityId;
    private Timestamp time;
    private Long personId;
}
