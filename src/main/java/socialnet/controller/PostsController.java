package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.PostRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PostRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.model.SearchOptions;
import socialnet.service.FindService;
import socialnet.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "posts-controller", description = "Get feeds. Get, update, delete, recover, find post, get users post, create post")
public class PostsController {
    private final PostService postsService;
    private final FindService findService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/feeds")
    @Operation(summary = "get all news")
    public CommonRs<List<PostRs>> getFeeds(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return postsService.getFeeds(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/users/{id}/wall")
    @Operation(summary = "get all post by author id")
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return postsService.getFeedsByAuthorId(id, authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/api/v1/users/{id}/wall")
    @Operation(summary = "create new post")
    public CommonRs<PostRs> createPost(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestBody PostRq postRq,
            @RequestParam(required = false, name = "publish_date") Long publishDate,
            @PathVariable int id) {
        log.info(String.valueOf(publishDate));
        return postsService.createPost(postRq, id, publishDate, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post/{id}")
    @Operation(summary = "get post by id")
    public CommonRs<PostRs> getPostById(@RequestHeader
                                        @Parameter(description = "Access Token", example = "JWT Token")
                                        String authorization,
                                        @PathVariable int id) {
        return postsService.getPostById(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}")
    @Operation(summary = "create new post")
    public CommonRs<PostRs> updateById(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable int id,
            @RequestBody PostRq postRq) {

        return postsService.updatePost(id, postRq, authorization);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/post/{id}")
    @Operation(summary = "delete post by id")
    public CommonRs<PostRs> deleteById(@RequestHeader @Parameter(description = "Access Token", example = "JWT Token")
                                       String authorization, @PathVariable int id) {
        return postsService.markAsDelete(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/recover")
    @Operation(summary = "recover post by id")
    public CommonRs<PostRs> recoverPostById(@RequestHeader
                                            @Parameter(description = "Access Token", example = "JWT Token")
                                            String authorization,
                                            @PathVariable int id) {
        return postsService.recoverPost(id, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post")
    @Operation(summary = "get posts by query")
    public CommonRs<List<PostRs>> getPostsByQuery(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestParam(required = false, defaultValue = "") String author,
            @RequestParam(required = false, name = "date_from", defaultValue = "0") Long dateFrom,
            @RequestParam(required = false, name = "date_to", defaultValue = "0") Long dateTo,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false, defaultValue = "") String text) {

        return findService.getPostsByQuery(SearchOptions.builder()
                .jwtToken(authorization)
                .author(author)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .tags(tags)
                .text(text)
                .offset(offset)
                .perPage(perPage)
                .build());
    }
}
