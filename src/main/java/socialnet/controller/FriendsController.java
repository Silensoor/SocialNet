package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.PersonRs;
import socialnet.service.FriendsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("/friends")
    @ResponseBody
    public CommonRs<List<PersonRs>> getFriends(@RequestHeader String authorization,
                                               @RequestParam(required = false, defaultValue = "0")
                                               Integer offset,
                                               @RequestParam(required = false, defaultValue = "20")
                                               Integer perPage) {
        return friendsService.getFriends(authorization, offset, perPage);
    }

    @PostMapping("/friends/block_unblock/{id}")
    @ResponseBody
    public HttpStatus userBlocks(@RequestHeader String authorization,
                                 @PathVariable(value = "id") Integer id) {
        return friendsService.userBlocks(authorization, id);
    }

    @GetMapping("/friends/outgoing_requests")
    @ResponseBody
    public CommonRs<List<PersonRs>> getOutgoingRequests(@RequestHeader String authorization,
                                                        @RequestParam(required = false, defaultValue = "0")
                                                        Integer offset,
                                                        @RequestParam(required = false, defaultValue = "20")
                                                        Integer perPage) {

        return friendsService.getOutgoingRequests(authorization, offset, perPage);
    }

    @GetMapping("/friends/recommendations")
    @ResponseBody
    public CommonRs<List<PersonRs>> getRecommendedFriends(@RequestHeader String authorization) {
        return friendsService.getRecommendedFriends(authorization);
    }

    @GetMapping("/friends/request")
    @ResponseBody
    public CommonRs<List<PersonRs>> getPotentialFriends(@RequestHeader String authorization,
                                                        @RequestParam(required = false, defaultValue = "0")
                                                        Integer offset,
                                                        @RequestParam(required = false, defaultValue = "20")
                                                        Integer perPage) {

        return friendsService.getPotentialFriends(authorization, offset, perPage);
    }

    @PostMapping("/friends/request/{id}")
    public CommonRs<ComplexRs> addFriend(@RequestHeader String authorization,
                                         @PathVariable(value = "id") Integer id) {
        return friendsService.addFriend(authorization, id);
    }

    @DeleteMapping("/friends/request/{id}")
    public CommonRs<ComplexRs> deleteFriendsRequest(@RequestHeader String authorization,
                                                    @PathVariable(value = "id") Integer id) {
        return friendsService.deleteFriendsRequest(authorization, id);
    }

    @PostMapping("/friends/{id}")
    public CommonRs<ComplexRs> sendFriendsRequest(@RequestHeader String authorization,
                                                  @PathVariable(value = "id") Integer id) {
        return friendsService.sendFriendsRequest(authorization, id);
    }

    @DeleteMapping("/friends/{id}")
    public CommonRs<ComplexRs> deleteFriend(@RequestHeader String authorization,
                                            @PathVariable(value = "id") Integer id) {
        return friendsService.deleteFriend(authorization, id);
    }
}
