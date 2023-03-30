package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socialnet.dto.CommonRs;
import socialnet.dto.PostRq;
import socialnet.dto.PostRs;
import socialnet.exception.RegisterException;
import socialnet.model.Post;
import socialnet.service.PostService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostsController {
    private final PostService postsService;

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
}
