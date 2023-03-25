package socialnet.model.db;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Message {
    private long id;
    private boolean isDeleted;
    private String messageText;
    private String readStatus;
    private Timestamp time;
    private long dialogId;
    private long authorId;
    private long recipientId;
}
