package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.PostRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PostRs;
import socialnet.service.PostService;

import java.text.ParseException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class PostsController {
    private final PostService postsService;

    @GetMapping("/api/v1/feeds")
    public CommonRs<List<PostRs>> getFeeds(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return postsService.getFeeds(jwtToken, offset, perPage);
    }
    @GetMapping("/api/v1/users/{id}/wall")
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader(name = "authorization") String jwtToken,
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage)
    {
        return postsService.getFeedsByAuthorId(id, jwtToken, offset, perPage);
    }

    @PostMapping("/api/v1/users/{id}/wall")
    public CommonRs<PostRs> createPost(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestBody PostRq postRq,
            @RequestParam(required = false, name = "publish_date") Integer publishDate,
            @PathVariable int id) {
        return postsService.createPost(postRq, id, publishDate, jwtToken);
    }

    @GetMapping("/api/v1/post/{id}")
    public CommonRs<PostRs> getPostById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        return postsService.getPostById(id, jwtToken);
    }

    @PutMapping("/api/v1/post/{id}")
    public CommonRs<PostRs> updateById(
            @RequestHeader(name = "authorization") String jwtToken,
            @PathVariable int id,
            @RequestBody PostRq postRq) {

        return postsService.updatePost(id, postRq, jwtToken);
    }

    @DeleteMapping("/api/v1/post/{id}")
    public CommonRs<PostRs> deleteById(@RequestHeader(name = "authorization") String jwtToken, @PathVariable int id) {
        return postsService.markAsDelete(id, jwtToken);
    }

    @PutMapping("/api/v1/post/{id}/recover")
    public CommonRs<PostRs> recoverPostById(@RequestHeader(name = "authorization") String jwtToken,
                                                            @PathVariable int id)
    {
        return postsService.recoverPost(id, jwtToken);
    }

    @GetMapping("/api/v1/post")
    public CommonRs<List<PostRs>> getPostsByQuery(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam(required = false) String author,
            @RequestParam(required = false, name = "date_from") Integer dateFrom,
            @RequestParam(required = false, name = "date_to") Integer dateTo,
            @RequestParam(required = false) int offset,
            @RequestParam(required = false) int perPage,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false) String text) throws ParseException {
        return postsService.getPostsByQuery(jwtToken, author, dateFrom, dateTo, offset, perPage, tags, text);

    }
}
