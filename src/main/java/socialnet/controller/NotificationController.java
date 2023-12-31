package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.NotificationRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.NotificationsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "notification-controller", description = "Get, read notifications")
public class NotificationController {

    private final NotificationsService notificationsService;

    @OnlineStatusUpdatable
    @GetMapping("/notifications")
    @ApiOperation(value = "get all notifications for user")
    public CommonRs<List<NotificationRs>> notifications(
            @RequestHeader @Parameter String authorization,
            @RequestParam(required = false, defaultValue = "10") @Parameter Integer itemPerPage,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset) {
        return notificationsService.getAllNotifications(itemPerPage, authorization, offset);
    }

    @OnlineStatusUpdatable
    @PutMapping("/notifications")
    @ApiOperation(value = "read notification")
    public CommonRs<List<NotificationRs>> notifications(
            @RequestHeader @Parameter String authorization,
            @RequestParam(required = false) @Parameter Integer id,
            @RequestParam(required = false) @Parameter Boolean all) {
        return notificationsService.putNotifications(all, id, authorization);
    }
}
