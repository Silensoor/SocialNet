package socialnet.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Like {
    private Long id;
    private String type;
    private Long entityId;
    private Timestamp time;
    private Long personId;
}
