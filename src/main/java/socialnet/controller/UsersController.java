package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.UserRq;
import socialnet.dto.PostRq;
import socialnet.service.LoginService;
import socialnet.service.users.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final LoginService loginService;
    private final SearchService searchService;
    private final FindUserService findUserService;
    private final FindPostService findPostService;
    private final UpdateService updateService;
    private final RecoverService recoverService;
    private final NewPostService newPostService;
    private final DeleteService deleteService;

    @GetMapping("/me")
    public Object Me(@RequestHeader(name = "authorization") String authorization) {
        return loginService.getMe(authorization);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findPostsByQuery(@RequestHeader("authorization") String authorization,
                                              @RequestParam Optional<Integer> age_from,
                                              @RequestParam Optional<Integer> age_to,
                                              @RequestParam Optional<String> city,
                                              @RequestParam Optional<String> country,
                                              @RequestParam Optional<String> first_name,
                                              @RequestParam Optional<String> last_name,
                                              @RequestParam Optional<Integer> offset,
                                              @RequestParam Optional<Integer> perPage) {

        return searchService.searchPostByQuery(authorization,
                age_from.orElse(null),
                age_to.orElse(null),
                city.orElse(null),
                country.orElse(null),
                first_name.orElse(null),
                last_name.orElse(null),
                offset.orElse(0),
                perPage.orElse(20));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUser(@RequestHeader("authorization") String authorization,
                                      Integer userId) {
        return findUserService.findUserById(authorization, userId);
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity<?> findPosts(@RequestHeader("authorization") String authorization,
                                       Long userId,
                                       @RequestParam Optional<Integer> itemPerPage,
                                       @RequestParam Optional<Integer> offset) {
        return findPostService.findPostsByUserId(authorization,
                userId,
                itemPerPage.orElse(20),
                offset.orElse(0));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserInfo(@RequestHeader("authorization") String authorization,
                                            @RequestBody UserRq userData) {
        return updateService.updateUserInfo(authorization, userData);
    }

    @PostMapping("/me/recover")
    public ResponseEntity<?> recoverUserInfo(@RequestHeader("authorization") String authorization) {
        return recoverService.recoverUser(authorization);
    }

//    @PostMapping("/{id}/wall")
//    public ResponseEntity<?> createNewPost(@RequestHeader("authorization") String authorization,
//                                           Integer id,
//                                           @RequestBody PostRq post,
//                                           @RequestParam Optional<Long> publish_date) {
//        return newPostService.createNewPost(authorization,
//                id,
//                post,
//                publish_date.orElse(System.currentTimeMillis()));
//    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(@RequestHeader("authorization") String authorization) {
        return deleteService.deleteUser(authorization);
    }

}
