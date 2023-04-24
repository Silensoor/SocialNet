package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.NotificationRq;
import socialnet.api.request.RegisterRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.PersonSettingsRs;
import socialnet.api.response.RegisterRs;
import socialnet.service.AccountService;
import socialnet.service.NotificationsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final NotificationsService notificationsService;

    @PostMapping("/register")
    public RegisterRs register(@Valid @RequestBody RegisterRq regRequest) {
        return accountService.getRegisterData(regRequest);
    }
    @GetMapping("/notifications")
    public CommonRs<List<PersonSettingsRs>> notifications(@RequestHeader String authorization){
        return  notificationsService.getNotificationByPerson(authorization);
    }
    @PutMapping("/notifications")
    public CommonRs<ComplexRs> notifications(@RequestHeader String authorization, @RequestBody NotificationRq notificationRq){
        return notificationsService.putNotificationByPerson(authorization,notificationRq);
    }
}
