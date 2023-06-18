package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.PostRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PostRs;
import socialnet.service.FindService;
import socialnet.service.PostService;

import java.text.ParseException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class PostsController {
    private final PostService postsService;
    private final FindService findService;

    @GetMapping("/api/v1/feeds")
    public CommonRs<List<PostRs>> getFeeds(
            @RequestHeader String authorization,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return postsService.getFeeds(authorization, offset, perPage);
    }

    @GetMapping("/api/v1/users/{id}/wall")
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader String authorization,
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return postsService.getFeedsByAuthorId(id, authorization, offset, perPage);
    }

    @PostMapping("/api/v1/users/{id}/wall")
    public CommonRs<PostRs> createPost(
            @RequestHeader String authorization,
            @RequestBody PostRq postRq,
            @RequestParam(required = false, name = "publish_date") Integer publishDate,
            @PathVariable int id) {
        return postsService.createPost(postRq, id, publishDate, authorization);
    }

    @GetMapping("/api/v1/post/{id}")
    public CommonRs<PostRs> getPostById(@RequestHeader String authorization, @PathVariable int id) {
        return postsService.getPostById(id, authorization);
    }

    @PutMapping("/api/v1/post/{id}")
    public CommonRs<PostRs> updateById(
            @RequestHeader String authorization,
            @PathVariable int id,
            @RequestBody PostRq postRq) {

        return postsService.updatePost(id, postRq, authorization);
    }

    @DeleteMapping("/api/v1/post/{id}")
    public CommonRs<PostRs> deleteById(@RequestHeader String authorization, @PathVariable int id) {
        return postsService.markAsDelete(id, authorization);
    }

    @PutMapping("/api/v1/post/{id}/recover")
    public CommonRs<PostRs> recoverPostById(@RequestHeader String authorization,
                                            @PathVariable int id) {
        return postsService.recoverPost(id, authorization);
    }

    @GetMapping("/api/v1/post")
    @ResponseBody
    public CommonRs<List<PostRs>> getPostsByQuery(
            @RequestHeader String authorization,
            @RequestParam(required = false, defaultValue = "") String author,
            @RequestParam(required = false, name = "date_from", defaultValue = "0") Long dateFrom,
            @RequestParam(required = false, name = "date_to", defaultValue = "0") Long dateTo,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false, defaultValue = "") String text) throws ParseException {
        return findService.getPostsByQuery(authorization, author, dateFrom, dateTo, offset, perPage, tags, text);

    }
}
