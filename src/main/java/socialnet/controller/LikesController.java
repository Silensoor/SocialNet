package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.LikeRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.LikesService;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/likes")
    public CommonRs<LikeRs> getLikes(
            @RequestHeader String authorization,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type) {

        return likesService.getLikes(authorization, itemId, type);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/likes")
    public CommonRs<LikeRs> putLike(
            @RequestHeader String authorization,
            @RequestBody LikeRq likeRq) {

        return likesService.putLike(authorization, likeRq);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/likes")
    public CommonRs<LikeRs> deleteLike(
            @RequestHeader String authorization,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type) {

        return likesService.deleteLike(authorization, itemId, type);
    }
}
