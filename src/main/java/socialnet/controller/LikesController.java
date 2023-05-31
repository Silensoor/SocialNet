package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.LikeRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.LikesService;

@RestController
@RequiredArgsConstructor
@Tag(name = "likes-controller", description = "Get likes, delete and put like")
public class LikesController {

    private final LikesService likesService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/likes")
    @Operation(summary = "get all my likes by comment or post")
    public CommonRs<LikeRs> getLikes(
            @RequestHeader  @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type) {

        return likesService.getLikes(itemId, type);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/likes")
    @Operation(summary = "put like on post or comment")
    public CommonRs<LikeRs> putLike(
            @RequestHeader  @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
            @RequestBody LikeRq likeRq) {

        return likesService.putLike(authorization, likeRq);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/likes")
    @Operation(summary = "delete like from post or comment")
    public CommonRs<LikeRs> deleteLike(
            @RequestHeader  @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type) {

        return likesService.deleteLike(authorization, itemId, type);
    }
}
