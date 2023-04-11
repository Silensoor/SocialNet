package socialnet.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.CommonRsListPersonRs;
import socialnet.api.response.ComplexRs;
import socialnet.service.FriendsService;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1")
@AllArgsConstructor
public class FriendsController {

    FriendsService friendsService;

    @GetMapping("/friends")
    @ResponseBody
    public CommonRsListPersonRs getFriendsUsingGET(@RequestHeader String authorization,
                                                   @RequestParam Optional<Integer> offset,
                                                   @RequestParam Optional<Integer> perPage) {
        return friendsService.getFriendsUsing(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @PostMapping("/friends/block_unblock/{id}")
    @ResponseBody
    public HttpStatus userBlocksUserUsingPOST(@RequestHeader String authorization,
                                              @PathVariable(value = "id") Integer id) {
        return friendsService.userBlocksUserUsingPOST(authorization, id);
    }

    @GetMapping("/friends/outgoing_requests")
    @ResponseBody
    public CommonRsListPersonRs getOutgoingRequestsUsingGET(@RequestHeader String authorization,
                                                            @RequestParam Optional<Integer> offset,
                                                            @RequestParam Optional<Integer> perPage) {

        return friendsService.getOutgoingRequestsUsingGET(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @GetMapping("/friends/recommendations")
    @ResponseBody
    public CommonRsListPersonRs getRecommendedFriendsUsingGET(@RequestHeader String authorization) {
        friendsService.getRecommendedFriendsUsingGET(authorization);
        return friendsService.getRecommendedFriendsUsingGET(authorization);
    }

    @GetMapping("/friends/request")
    @ResponseBody
    public CommonRsListPersonRs getPotentialFriendsUsingGET(@RequestHeader String authorization,
                                                            @RequestParam Optional<Integer> offset,
                                                            @RequestParam Optional<Integer> perPage) {

        return friendsService.getPotentialFriendsUsingGET(authorization,
                offset.orElse(0),
                perPage.orElse(20));
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
    @ResponseBody
    public CommonRsComplexRs deleteFriendUsingDELETE(@RequestHeader String authorization,
                                                     @PathVariable(value = "id") Integer id) {
        return friendsService.deleteFriendUsingDELETE(authorization, id);
    }
}
