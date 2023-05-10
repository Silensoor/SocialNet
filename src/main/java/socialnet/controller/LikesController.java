package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.LikeRs;

@RestController
@RequiredArgsConstructor
public class LikesController {

    //private final LikesService likesService;

    @GetMapping("/api/v1/likes")
    public CommonRs<LikeRs> getLikes(
            @RequestHeader String authorization,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type) {

        //return likesService.getLikes(authorization, itemId, type);
        return null;
    }

    @PutMapping("/api/v1/likes")
    public CommonRs<LikeRs> putLike(
            @RequestHeader String authorization,
            @RequestBody LikeRq likeRq) {

        //return likesService.putLike(authorization, likeRq);
        return null;
    }

    @DeleteMapping("/api/v1/likes")
    public CommonRs<LikeRs> deleteLike(
            @RequestHeader String authorization,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type) {

        //return likesService.deleteLike(authorization, itemId, type);
        return null;

    }
}
