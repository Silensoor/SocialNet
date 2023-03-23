package socialnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import socialnet.service.FriendsService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/friends")
public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController() {
        this.friendsService = new FriendsService();
    }

    @GetMapping("/")
    public ResponseEntity<?> getFriendsUsingGET(@RequestParam String authorization,
                                                @RequestParam Optional<Integer> offset,
                                                @RequestParam Optional<Integer> perPage) {

        return friendsService.getFriendsUsing(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @PostMapping("/block_unblock/{id}")
    public ResponseEntity<?> userBlocksUserUsingPOST(@RequestParam String authorization,
                                                @RequestParam Integer id) {

        return friendsService.getFriendsBlockUnblock(authorization, id);
    }

    @GetMapping("/outgoing_requests")
    public ResponseEntity<?> getOutgoingRequestsUsingGET(@RequestParam String authorization,
                                                @RequestParam Optional<Integer> offset,
                                                @RequestParam Optional<Integer> perPage) {

        return friendsService.getFriendsUsing(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendedFriendsUsingGET(@RequestParam String authorization) {

        return friendsService.getFriendsUsing(authorization, 0, 20);
    }

    @GetMapping("/request")
    public ResponseEntity<?> getPotentialFriendsUsingGET(@RequestParam String authorization,
                                                         @RequestParam Optional<Integer> offset,
                                                         @RequestParam Optional<Integer> perPage) {

        return friendsService.getFriendsUsing(authorization,
                offset.orElse(0),
                perPage.orElse(20));
    }

    @GetMapping("/request/{id}")
    public ResponseEntity<?> addFriendUsingPOST(@RequestParam String authorization,
                                                @RequestParam Integer id) {

        return friendsService.getFriendsUsingId(authorization, id);
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<?> deleteSentFriendshipRequestUsingDELETE(@RequestParam String authorization,
                                                @RequestParam Integer id) {

        return friendsService.getFriendsUsingId(authorization, id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> sendFriendshipRequestUsingPOST(@RequestParam String authorization,
                                                     @RequestParam Integer id) {

        return friendsService.getFriendsBlockUnblock(authorization, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFriendUsingDELETE(@RequestParam String authorization,
                                                     @RequestParam Integer id) {
        return friendsService.getFriendsUsingId(authorization, id);
    }
}
