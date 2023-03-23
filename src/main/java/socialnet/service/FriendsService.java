package socialnet.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import socialnet.api.friends.CommonRsListPersonRs;

public class FriendsService {


    public ResponseEntity<?> getFriendsUsing(String authorization, Integer offset, Integer perPage) {

        return new ResponseEntity<>(new CommonRsListPersonRs(), HttpStatus.OK);
    }


    public ResponseEntity<?> getFriendsBlockUnblock(String authorization, Integer id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> getFriendsUsingId(String authorization, Integer id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
