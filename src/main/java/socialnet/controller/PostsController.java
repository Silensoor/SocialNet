package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socialnet.dto.CommonRs;
import socialnet.dto.PostRq;
import socialnet.dto.PostRs;
import socialnet.service.PostService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostsController {
    private final PostService postsService;

    @GetMapping("/api/v1/feeds")
    public ResponseEntity<CommonRs<List<PostRs>>> getFeeds(@RequestHeader(name = "authorization") String jwtToken, @RequestParam int offset, @RequestParam int perPage) {
        return postsService.getFeeds(jwtToken, offset, perPage);
    }
    @PostMapping("/api/v1/users/{id}/wall")
    public ResponseEntity<CommonRs<PostRs>> createPost(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestBody PostRq postRq,
            @RequestParam(required = false, name = "publish_date") int publishDate,
            @PathVariable int id) {
        return postsService.createPost(postRq, id, publishDate, jwtToken);
    }
}
