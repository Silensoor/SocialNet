package socialnet.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socialnet.api.friends.CommonRsListPersonRs;
import socialnet.dto.CommonRsComplexRs;
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
    @ResponseBody
    public CommonRsComplexRs addFriendUsingPOST(@RequestHeader String authorization,
                                                @PathVariable(value = "id") Integer id) {

        return friendsService.addFriendUsingPOST(authorization, id);
    }

    @DeleteMapping("/friends/request/{id}")
    @ResponseBody
    public CommonRsComplexRs deleteSentFriendshipRequestUsingDELETE(@RequestHeader String authorization,
                                                                    @PathVariable(value = "id") Integer id) {

        return friendsService.deleteSentFriendshipRequestUsingDELETE(authorization, id);
    }

    @PostMapping("/friends/{id}")
    @ResponseBody
    public CommonRsComplexRs sendFriendshipRequestUsingPOST(@RequestHeader String authorization,
                                                            @PathVariable(value = "id") Integer id) {

        return friendsService.sendFriendshipRequestUsingPOST(authorization, id);
    }

    @DeleteMapping("/friends/{id}")
    @ResponseBody
    public CommonRsComplexRs deleteFriendUsingDELETE(@RequestHeader String authorization,
                                                     @PathVariable(value = "id") Integer id) {
        return friendsService.deleteFriendUsingDELETE(authorization, id);
    }
}
