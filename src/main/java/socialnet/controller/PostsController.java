package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.PostRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PostRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.FindService;
import socialnet.service.PostService;

import java.text.ParseException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "posts-controller", description = "Get feeds. Get, update, delete, recover, find post, get users post, create post")
public class PostsController {
    private final PostService postsService;
    private final FindService findService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/feeds")
    @ApiOperation(value = "get all news")
    public CommonRs<List<PostRs>> getFeeds(
            @RequestHeader String authorization,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return postsService.getFeeds(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/users/{id}/wall")
    @ApiOperation(value = "get all post by author id")
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader String authorization,
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return postsService.getFeedsByAuthorId(id, authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/api/v1/users/{id}/wall")
    @ApiOperation(value = "create new post")
    public CommonRs<PostRs> createPost(
            @RequestHeader String authorization,
            @RequestBody PostRq postRq,
            @RequestParam(required = false, name = "publish_date") Long publishDate,
            @PathVariable int id) {
        System.out.println(publishDate);
        return postsService.createPost(postRq, id, publishDate, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post/{id}")
    @ApiOperation(value = "get post by id")
    public CommonRs<PostRs> getPostById(@RequestHeader String authorization, @PathVariable int id) {
        return postsService.getPostById(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}")
    @ApiOperation(value = "create new post")
    public CommonRs<PostRs> updateById(
            @RequestHeader String authorization,
            @PathVariable int id,
            @RequestBody PostRq postRq) {

        return postsService.updatePost(id, postRq, authorization);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/post/{id}")
    @ApiOperation(value = "delete post by id")
    public CommonRs<PostRs> deleteById(@RequestHeader String authorization, @PathVariable int id) {
        return postsService.markAsDelete(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/recover")
    @ApiOperation(value = "recover post by id")
    public CommonRs<PostRs> recoverPostById(@RequestHeader String authorization,
                                            @PathVariable int id) {
        return postsService.recoverPost(id, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post")
    @ApiOperation(value = "get posts by query")
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
