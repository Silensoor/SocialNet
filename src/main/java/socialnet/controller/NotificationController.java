package socialnet.controller;

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
public class NotificationController {

    private final NotificationsService notificationsService;

    @OnlineStatusUpdatable
    @GetMapping("/notifications")
    public CommonRs<List<NotificationRs>> notifications(
        @RequestHeader String authorization,
        @RequestParam(required = false, defaultValue = "10") Integer itemPerPage,
        @RequestParam(required = false, defaultValue = "0") Integer offset)
    {
        return notificationsService.getAllNotifications(itemPerPage, authorization, offset);
    }

    @OnlineStatusUpdatable
    @PutMapping("/notifications")
    public CommonRs<List<NotificationRs>> notifications(
        @RequestHeader String authorization,
        @RequestParam(required = false) Integer id,
        @RequestParam(required = false) Boolean all)
    {
        return notificationsService.putNotifications(all, id, authorization);
    }
}
