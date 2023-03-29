package socialnet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socialnet.service.FriendsService;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1")
public class FriendsController {
    @Autowired
    FriendsService friendsService;

    @GetMapping("/friends")
    public ResponseEntity<?> getFriendsUsingGET(@RequestHeader String authorization,
                                                @RequestParam Optional<Integer> offset,
                                                @RequestParam Optional<Integer> perPage) {
        return friendsService.getFriendsUsing(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @PostMapping("/friends/block_unblock/{id}")
    public ResponseEntity<?> userBlocksUserUsingPOST(@RequestHeader String authorization,
                                                     @PathVariable(value = "id") Integer id) {
        return friendsService.userBlocksUserUsingPOST(authorization, id);
    }

    @GetMapping("/friends/outgoing_requests")
    public ResponseEntity<?> getOutgoingRequestsUsingGET(@RequestHeader String authorization,
                                                @RequestParam Optional<Integer> offset,
                                                @RequestParam Optional<Integer> perPage) {

        return friendsService.getOutgoingRequestsUsingGET(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<?> getRecommendedFriendsUsingGET(@RequestHeader String authorization) {

        return friendsService.getRecommendedFriendsUsingGET(authorization);
    }

    @GetMapping("/friends/request")
    public ResponseEntity<?> getPotentialFriendsUsingGET(@RequestHeader String authorization,
                                                         @RequestParam Optional<Integer> offset,
                                                         @RequestParam Optional<Integer> perPage) {

        return friendsService.getPotentialFriendsUsingGET(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @PostMapping("/friends/request/{id}")
    public ResponseEntity<?> addFriendUsingPOST(@RequestHeader String authorization,
                                                @PathVariable(value = "id") Integer id) {

        return friendsService.addFriendUsingPOST(authorization, id);
    }

    @DeleteMapping("/friends/request/{id}")
    public ResponseEntity<?> deleteSentFriendshipRequestUsingDELETE(@RequestHeader String authorization,
                                                @RequestParam Integer id) {

        return friendsService.deleteSentFriendshipRequestUsingDELETE(authorization, id);
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<?> sendFriendshipRequestUsingPOST(@RequestHeader String authorization,
                                                     @RequestParam Integer id) {

        return friendsService.sendFriendshipRequestUsingPOST(authorization, id);
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<?> deleteFriendUsingDELETE(@RequestHeader String authorization,
                                                     @RequestParam Integer id) {
        return friendsService.deleteFriendUsingDELETE(authorization, id);
    }
}
