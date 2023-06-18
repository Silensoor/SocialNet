package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
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
            @RequestHeader @Parameter String authorization,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter Integer perPage) {
        return postsService.getFeeds(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/users/{id}/wall")
    @ApiOperation(value = "get all post by author id")
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader @Parameter String authorization,
            @PathVariable(name = "id") @Parameter Long id,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter Integer perPage) {
        return postsService.getFeedsByAuthorId(id, authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/api/v1/users/{id}/wall")
    @ApiOperation(value = "create new post")
    public CommonRs<PostRs> createPost(
            @RequestHeader @Parameter String authorization,
            @RequestBody @Parameter PostRq postRq,
            @RequestParam(required = false, name = "publish_date") @Parameter Integer publishDate,
            @PathVariable @Parameter int id) {
        return postsService.createPost(postRq, id, publishDate, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post/{id}")
    @ApiOperation(value = "get post by id")
    public CommonRs<PostRs> getPostById(@RequestHeader @Parameter String authorization, @PathVariable @Parameter int id) {
        return postsService.getPostById(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}")
    @ApiOperation(value = "create new post")
    public CommonRs<PostRs> updateById(
            @RequestHeader @Parameter String authorization,
            @PathVariable @Parameter int id,
            @RequestBody @Parameter PostRq postRq) {

        return postsService.updatePost(id, postRq, authorization);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/post/{id}")
    @ApiOperation(value = "delete post by id")
    public CommonRs<PostRs> deleteById(@RequestHeader @Parameter String authorization, @PathVariable @Parameter int id) {
        return postsService.markAsDelete(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/recover")
    @ApiOperation(value = "recover post by id")
    public CommonRs<PostRs> recoverPostById(@RequestHeader @Parameter String authorization,
                                            @PathVariable @Parameter int id) {
        return postsService.recoverPost(id, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post")
    @ApiOperation(value = "get posts by query")
    public CommonRs<List<PostRs>> getPostsByQuery(
            @RequestHeader @Parameter String authorization,
            @RequestParam(required = false, defaultValue = "") @Parameter String author,
            @RequestParam(required = false, name = "date_from", defaultValue = "0") @Parameter Long dateFrom,
            @RequestParam(required = false, name = "date_to", defaultValue = "0") @Parameter Long dateTo,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter Integer perPage,
            @RequestParam(required = false) @Parameter String[] tags,
            @RequestParam(required = false, defaultValue = "") @Parameter String text) throws ParseException {
        return findService.getPostsByQuery(authorization, author, dateFrom, dateTo, offset, perPage, tags, text);

    }
}
