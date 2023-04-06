package socialnet.service.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.dto.PostRq;


@Service
public class NewPostService {
    public ResponseEntity<?> createNewPost(String authorization, Integer id, PostRq post,
                                           Long publish_date) {

        // TODO: 22.03.2023 : 200, 400 - "Name of error", 401 - Unauthorized , 403 - Forbidden

        return new ResponseEntity<>(post, HttpStatus.OK);

    }
}
