package socialnet.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRsListPersonRs;
import socialnet.dto.CommonRsComplexRs;
import socialnet.service.FriendsService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("/friends")
    public CommonRsListPersonRs getFriends(@RequestHeader String authorization,
                                           @RequestParam(required = false, defaultValue = "0") Integer offset,
                                           @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return friendsService.getFriends(authorization, offset, perPage);
    }

    @PostMapping("/friends/block_unblock/{id}")
    public HttpStatus userBlocks(@RequestHeader String authorization,
                                 @PathVariable(value = "id") Integer id) {
        return friendsService.userBlocks(authorization, id);
    }

    @GetMapping("/friends/outgoing_requests")
    public CommonRsListPersonRs getOutgoingRequests(@RequestHeader String authorization,
                                                    @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                    @RequestParam(required = false, defaultValue = "20") Integer perPage) {
        return friendsService.getOutgoingRequests(authorization, offset, perPage);
    }

    @GetMapping("/friends/recommendations")
    public CommonRsListPersonRs getRecommendedFriends(@RequestHeader String authorization) {
        return friendsService.getRecommendedFriends(authorization);
    }

    @GetMapping("/friends/request")
    public CommonRsListPersonRs getPotentialFriends(@RequestHeader String authorization,
                                                    @RequestParam(required = false, defaultValue = "0")
                                                    Integer offset,
                                                    @RequestParam(required = false, defaultValue = "20")
                                                    Integer perPage) {
        return friendsService.getPotentialFriends(authorization, offset, perPage);
    }

    @PostMapping("/friends/request/{id}")
    public CommonRsComplexRs addFriend(@RequestHeader String authorization,
                                       @PathVariable(value = "id") Integer id) {
        return friendsService.addFriend(authorization, id);
    }

    @DeleteMapping("/friends/request/{id}")
    public CommonRsComplexRs deleteFriendsRequest(@RequestHeader String authorization,
                                                  @PathVariable(value = "id") Integer id) {
        return friendsService.deleteFriendsRequest(authorization, id);
    }

    @PostMapping("/friends/{id}")
    public CommonRsComplexRs sendFriendsRequest(@RequestHeader String authorization,
                                                @PathVariable(value = "id") Integer id) {
        return friendsService.sendFriendsRequest(authorization, id);
    }

    @DeleteMapping("/friends/{id}")
    public CommonRsComplexRs deleteFriend(@RequestHeader String authorization,
                                          @PathVariable(value = "id") Integer id) {
        return friendsService.deleteFriend(authorization, id);
    }
}
