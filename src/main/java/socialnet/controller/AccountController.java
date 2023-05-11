package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.EmailRq;
import socialnet.api.request.PasswordSetRq;
import socialnet.api.request.PersonSettingsRq;
import socialnet.api.request.RegisterRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.RegisterRs;
import socialnet.repository.PersonSettingRepository;
import socialnet.service.AccountService;
import socialnet.service.EmailService;
import socialnet.service.NotificationsService;
import socialnet.service.PersonService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final PersonSettingRepository personSettingRepository;
    private final NotificationsService notificationsService;
    private final EmailService emailService;
    private final PersonService personService;

    @PutMapping("/email/recovery")
    public void emailSet(@RequestHeader String authorization) {
        emailService.shiftEmailConfirm(authorization);
    }

    @PutMapping("/email")
    public RegisterRs setNewEmail(@RequestBody EmailRq emailRq) {return personService.setNewEmail(emailRq);}

    @PutMapping("/password/recovery")
    public void passwordChangeConfirm(@RequestHeader String authorization) {
        emailService.passwordChangeConfirm(authorization);
    }
    @PutMapping("/password/reset")
    public PasswordSetRq resetPassword(@RequestHeader String authorization,
                                       @RequestBody PasswordSetRq passwordSetRq) {
        return personService.resetPassword(authorization, passwordSetRq);
    }
    @PutMapping("/password/set")
    public PasswordSetRq setNewPassword(@RequestHeader String authorization,
                                        @RequestBody PasswordSetRq passwordSetRq) {
        return personService.resetPassword(authorization, passwordSetRq);
    }
    @PostMapping("/register")
    public RegisterRs register(@Valid @RequestBody RegisterRq regRequest) {
        return accountService.getRegisterData(regRequest);
    }
    @GetMapping("/notifications")
    public CommonRs notifications(@RequestHeader String authorization){
        return personService.getPersonSettings(authorization);
    }

    @PutMapping("/notifications")
    public CommonRs saveSettings(@RequestHeader String authorization,
                                 @RequestBody PersonSettingsRq personSettingsRq) {
        return personService.setSetting(authorization, personSettingsRq);
    }

//    @PutMapping("/notifications")
//    public CommonRs<ComplexRs> notifications(@RequestHeader String authorization, @RequestBody NotificationRq notificationRq){
//        return notificationsService.putNotificationByPerson(authorization,notificationRq);
//    }
}
