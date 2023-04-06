package socialnet.service.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.response.PostRs;


import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    public ResponseEntity<?> searchPostByQuery(String authorization,
                                               Integer age_from,
                                               Integer age_to,
                                               String city,
                                               String country,
                                               String first_name,
                                               String last_name,
                                               Integer offset,
                                               Integer perPage) {

        // TODO: 22.03.2023 : 200, 400 - "Name of error", 401 - Unauthorized , 403 - Forbidden

        List<PostRs> posts = new ArrayList<>();

        return new ResponseEntity<>(posts, HttpStatus.OK);

    }
}
