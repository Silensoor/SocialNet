package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LikeRq;
import socialnet.api.response.LikeRs;
import socialnet.dto.CommonRs;
import socialnet.service.LikesService;
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @GetMapping("/api/v1/likes")
    public CommonRs<LikeRs> getLikes(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type){
        return likesService.getLikes(jwtToken, itemId, type);
    }
    @PutMapping("/api/v1/likes")
    public CommonRs<LikeRs> putLike(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestBody LikeRq likeRq) {
        return likesService.putLike(jwtToken, likeRq);
    }

    public CommonRs<LikeRs> deleteLike(
            @RequestHeader(name = "authorization") String jwtToken,
            @RequestParam(name = "item_id") Integer itemId,
            @RequestParam String type) {
        return likesService.deleteLike(jwtToken, itemId, type);

    }



}
