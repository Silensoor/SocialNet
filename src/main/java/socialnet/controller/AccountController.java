package socialnet.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.EmailRq;
import socialnet.api.request.PasswordSetRq;
import socialnet.api.request.PersonSettingsRq;
import socialnet.api.request.RegisterRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.RegisterRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.repository.PersonSettingRepository;
import socialnet.service.AccountService;
import socialnet.service.EmailService;
import socialnet.service.NotificationsService;
import socialnet.service.PersonService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "account-controller", description = "Working with password, email and registration")
public class AccountController {
    private final AccountService accountService;
    private final PersonSettingRepository personSettingRepository;
    private final NotificationsService notificationsService;
    private final EmailService emailService;
    private final PersonService personService;

    @OnlineStatusUpdatable
    @PutMapping("/email/recovery")
    @ApiOperation(value = "user email recovery")
    public void emailSet(@RequestHeader String authorization) {
        emailService.shiftEmailConfirm(authorization);
    }

    @PutMapping("/email")
    @ApiOperation(value = "set email")
    public RegisterRs setNewEmail(@RequestBody EmailRq emailRq) {return personService.setNewEmail(emailRq);}

    @OnlineStatusUpdatable
    @PutMapping("/password/recovery")
    @ApiOperation(value = "user password recovery")
    public void passwordChangeConfirm(@RequestHeader String authorization) {
        emailService.passwordChangeConfirm(authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/password/reset")
    @ApiOperation(value = "user password reset")
    public RegisterRs resetPassword(@RequestHeader String authorization,
                                       @RequestBody PasswordSetRq passwordSetRq) {
        return personService.resetPassword(authorization, passwordSetRq);
    }

    @OnlineStatusUpdatable
    @PutMapping("/password/set")
    @ApiOperation(value = "set user password")
    public RegisterRs setNewPassword(@RequestHeader String authorization,
                                        @RequestBody PasswordSetRq passwordSetRq) {
        return personService.resetPassword(authorization, passwordSetRq);
    }

    @PostMapping("/register")
    @ApiOperation(value = "user registration")
    public RegisterRs register(@Valid @RequestBody RegisterRq regRequest) {
        return accountService.getRegisterData(regRequest);
    }

    @OnlineStatusUpdatable
    @GetMapping("/notifications")
    @ApiOperation(value = "get user's notifications properties")
    public CommonRs notifications(@RequestHeader String authorization){
        return personService.getPersonSettings(authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/notifications")
    @ApiOperation(value = "edit notifications properties")
    public CommonRs saveSettings(@RequestHeader String authorization,
                                 @RequestBody PersonSettingsRq personSettingsRq) {
        return personService.setSetting(authorization, personSettingsRq);
    }

}
