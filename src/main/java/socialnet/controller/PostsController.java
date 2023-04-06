package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PostRs;
import socialnet.dto.PostRq;
import socialnet.service.PostService;
import socialnet.service.users.FindPostService;

import java.text.ParseException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class PostsController {
    private final PostService postsService;

    private final FindPostService findPostService;

    @GetMapping("/api/v1/feeds")
    public ResponseEntity<CommonRs<List<PostRs>>> getFeeds(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam int offset,
            @RequestParam int perPage) {
        CommonRs<List<PostRs>> commonRs = postsService.getFeeds(jwtToken, offset, perPage);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @PostMapping("/api/v1/users/{id}/wall")
    public ResponseEntity<CommonRs<PostRs>> createPost(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestBody PostRq postRq,
            @RequestParam(required = false, name = "publish_date") Integer publishDate,
            @PathVariable int id) {
        CommonRs<PostRs> commonRs = postsService.createPost(postRq, id, publishDate, jwtToken);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<CommonRs<PostRs>> getPostById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        CommonRs<PostRs> commonRs = postsService.getPostById(id, jwtToken);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }

    @PutMapping("/api/v1/post/{id}")
    public ResponseEntity<CommonRs<PostRs>> updateById(
            @RequestHeader(name = "authorization") String jwtToken,
            @PathVariable int id,
            @RequestBody PostRq postRq) {
        CommonRs<PostRs> commonRs = postsService.updatePost(id, postRq, jwtToken);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @DeleteMapping("/api/v1/post/{id}")
    public ResponseEntity<CommonRs<PostRs>> deleteById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        CommonRs<PostRs> commonRs = postsService.markAsDelete(id, jwtToken);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @PutMapping("/api/v1/post/{id}/recover")
    public ResponseEntity<CommonRs<PostRs>> recoverPostById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        CommonRs<PostRs> commonRs = postsService.recoverPost(id, jwtToken);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @GetMapping("/api/v1/post")
    public CommonRs<List<PostRs>> getPostsByQuery(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam(required = false) String author,
            @RequestParam(required = false, name = "date_from")Integer dateFrom,
            @RequestParam(required = false, name = "date_to") Integer dateTo,
            @RequestParam(required = false) int offset,
            @RequestParam(required = false) int perPage,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false) String text) throws ParseException {
        return findPostService.getPostsByQuery(jwtToken, author, dateFrom, dateTo, offset, perPage, tags, text);

    }
}
