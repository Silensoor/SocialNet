package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.PostRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PostRs;
import socialnet.service.FindService;
import socialnet.service.PostService;

import java.text.ParseException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class PostsController {
    private final PostService postsService;
    private final FindService findService;

    @GetMapping("/api/v1/feeds")
    public ResponseEntity<CommonRs<List<PostRs>>> getFeeds(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return new ResponseEntity<>(postsService.getFeeds(jwtToken, offset, perPage), HttpStatus.OK);
    }

    @PostMapping("/api/v1/users/{id}/wall")
    public ResponseEntity<CommonRs<PostRs>> createPost(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestBody PostRq postRq,
            @RequestParam(required = false, name = "publish_date") Integer publishDate,
            @PathVariable int id) {
        return new ResponseEntity<>(postsService.createPost(postRq, id, publishDate, jwtToken), HttpStatus.OK);
    }

    @GetMapping("/api/v1/post/{id}")
    public ResponseEntity<CommonRs<PostRs>> getPostById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        return new ResponseEntity<>(postsService.getPostById(id, jwtToken), HttpStatus.OK);
    }

    @PutMapping("/api/v1/post/{id}")
    public ResponseEntity<CommonRs<PostRs>> updateById(
            @RequestHeader(name = "authorization") String jwtToken,
            @PathVariable int id,
            @RequestBody PostRq postRq) {
        return new ResponseEntity<>(postsService.updatePost(id, postRq, jwtToken), HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/post/{id}")
    public ResponseEntity<CommonRs<PostRs>> deleteById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        return new ResponseEntity<>(postsService.markAsDelete(id, jwtToken), HttpStatus.OK);
    }

    @PutMapping("/api/v1/post/{id}/recover")
    public ResponseEntity<CommonRs<PostRs>> recoverPostById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        return new ResponseEntity<>(postsService.recoverPost(id, jwtToken), HttpStatus.OK);
    }

    @GetMapping("/api/v1/post")
    @ResponseBody
    public CommonRs<List<PostRs>> getPostsByQuery(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam(required = false, defaultValue = "") String author,
            @RequestParam(required = false, name = "date_from", defaultValue = "0") Long dateFrom,
            @RequestParam(required = false, name = "date_to", defaultValue = "0") Long dateTo,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false, defaultValue = "") String text) throws ParseException {
        return findService.getPostsByQuery(jwtToken, author, dateFrom, dateTo, offset, perPage, tags, text);

    }
}
