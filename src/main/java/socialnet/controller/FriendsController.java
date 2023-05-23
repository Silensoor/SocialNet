package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "friends-controller", description = "Get recommended or potential friends. Add, delete, get friends. Send, delete friendship request")
public class FriendsController {
    private final FriendsService friendsService;

    @OnlineStatusUpdatable
    @GetMapping("/friends")
    @ApiOperation(value = "get friends of current user")
    public CommonRs<List<PersonRs>> getFriends(
            @RequestHeader @Parameter String authorization,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter Integer perPage) {
        return friendsService.getFriends(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/block_unblock/{id}")
    @ApiOperation(value = "block or unblock (if user in block) user by user")
    public HttpStatus userBlocks(@RequestHeader @Parameter String authorization,
                                 @PathVariable(value = "id") @Parameter Integer id) {
        return friendsService.userBlocks(authorization, id);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/outgoing_requests")
    @ApiOperation(value = "get outgoing requests by user")
    public CommonRs<List<PersonRs>> getOutgoingRequests(
            @RequestHeader @Parameter String authorization,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter Integer perPage) {
        return friendsService.getOutgoingRequests(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/recommendations")
    @ApiOperation(value = "get recommendation friends")
    public CommonRs<List<PersonRs>> getRecommendedFriends(@RequestHeader @Parameter String authorization) {
        return friendsService.getRecommendedFriends(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/request")
    @ApiOperation(value = "get potential friends of current user")
    public CommonRs<List<PersonRs>> getPotentialFriends(
            @RequestHeader @Parameter String authorization,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter Integer perPage) {
        return friendsService.getPotentialFriends(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/request/{id}")
    @ApiOperation(value = "add friend by id")
    public CommonRs<ComplexRs> addFriend(@RequestHeader @Parameter String authorization,
                                         @PathVariable(value = "id") @Parameter Integer id) {
        return friendsService.addFriend(authorization, id);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/friends/request/{id}")
    @ApiOperation(value = "decline friendship request by id")
    public CommonRs<ComplexRs> deleteFriendsRequest(@RequestHeader @Parameter String authorization,
                                                    @PathVariable(value = "id") @Parameter Integer id) {
        return friendsService.deleteFriendsRequest(authorization, id);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/{id}")
    @ApiOperation(value = "send friendship request by id of another user")
    public CommonRs<ComplexRs> sendFriendsRequest(@RequestHeader @Parameter String authorization,
                                                  @PathVariable(value = "id") @Parameter Integer id) {
        return friendsService.sendFriendsRequest(authorization, id);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/friends/{id}")
    @ApiOperation(value = "delete friend by id")
    public CommonRs<ComplexRs> deleteFriend(@RequestHeader @Parameter String authorization,
                                            @PathVariable(value = "id") @Parameter Integer id) {
        return friendsService.deleteFriend(authorization, id);
    }
}
