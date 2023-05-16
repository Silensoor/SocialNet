package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.PersonRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.FriendsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    @OnlineStatusUpdatable
    @GetMapping("/friends")
    public CommonRs<List<PersonRs>> getFriends(@RequestHeader String authorization,
                                               @RequestParam(required = false, defaultValue = "0")
                                               Integer offset,
                                               @RequestParam(required = false, defaultValue = "20")
                                               Integer perPage)
    {
        return friendsService.getFriends(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/block_unblock/{id}")
    public HttpStatus userBlocks(@RequestHeader String authorization,
                                 @PathVariable(value = "id") Integer id)
    {
        return friendsService.userBlocks(authorization, id);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/outgoing_requests")
    public CommonRs<List<PersonRs>> getOutgoingRequests(@RequestHeader String authorization,
                                                        @RequestParam(required = false, defaultValue = "0")
                                                        Integer offset,
                                                        @RequestParam(required = false, defaultValue = "20")
                                                        Integer perPage)
    {

        return friendsService.getOutgoingRequests(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/recommendations")
    public CommonRs<List<PersonRs>> getRecommendedFriends(@RequestHeader String authorization)
    {
        return friendsService.getRecommendedFriends(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/request")
    public CommonRs<List<PersonRs>> getPotentialFriends(@RequestHeader String authorization,
                                                        @RequestParam(required = false, defaultValue = "0")
                                                        Integer offset,
                                                        @RequestParam(required = false, defaultValue = "20")
                                                        Integer perPage)
    {
        return friendsService.getPotentialFriends(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/request/{id}")
    public CommonRs<ComplexRs> addFriend(@RequestHeader String authorization,
                                         @PathVariable(value = "id") Integer id)
    {
        return friendsService.addFriend(authorization, id);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/friends/request/{id}")
    public CommonRs<ComplexRs> deleteFriendsRequest(@RequestHeader String authorization,
                                                    @PathVariable(value = "id") Integer id)
    {
        return friendsService.deleteFriendsRequest(authorization, id);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/{id}")
    public CommonRs<ComplexRs> sendFriendsRequest(@RequestHeader String authorization,
                                                  @PathVariable(value = "id") Integer id)
    {
        return friendsService.sendFriendsRequest(authorization, id);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/friends/{id}")
    public CommonRs<ComplexRs> deleteFriend(@RequestHeader String authorization,
                                            @PathVariable(value = "id") Integer id)
    {
        return friendsService.deleteFriend(authorization, id);
    }
}
