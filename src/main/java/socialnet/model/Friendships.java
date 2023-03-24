package socialnet.model;

import lombok.Data;
import socialnet.model.enums.FriendshipStatusTypes;

import java.sql.Timestamp;

@Data
public class Friendships {
    private long id;
    private Timestamp sentTime;
    private long dstPersonId;
    private long srcPersonId;
    private FriendshipStatusTypes statusName;
}
