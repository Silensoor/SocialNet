package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.NotificationRs;
import socialnet.service.notifications.NotificationsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationsService notificationsService;
    @GetMapping("/notifications")
    public CommonRs<List<NotificationRs>> notifications(@RequestHeader String authorization,
                                                        @RequestParam(required = false, defaultValue = "10")Integer itemPerPage,
                                                        @RequestParam(required = false, defaultValue = "0") Integer offset){
        return notificationsService.getAllNotification(itemPerPage,authorization,offset);
    }

    @PostMapping("/notifications")
    public CommonRs<List<NotificationRs>> notifications(@RequestHeader String authorization,
                                                        @RequestParam Integer id,
                                                        @RequestParam(required = false,defaultValue = "false")Boolean all){
        return null;
    }
}
