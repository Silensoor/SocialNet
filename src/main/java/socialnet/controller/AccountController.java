package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.EmailRq;
import socialnet.api.request.PasswordSetRq;
import socialnet.api.request.PersonSettingsRq;
import socialnet.api.request.RegisterRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.PersonSettingsRs;
import socialnet.api.response.RegisterRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.repository.PersonSettingRepository;
import socialnet.service.AccountService;
import socialnet.service.EmailService;
import socialnet.service.NotificationsService;
import socialnet.service.PersonService;

import javax.validation.Valid;
import java.util.List;

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
    @Operation(summary = "user email recovery")
    public void emailSet(@RequestHeader @Parameter(description =  "Access Token", example = "JWT Token") String authorization) {
        emailService.shiftEmailConfirm(authorization);
    }

    @PutMapping("/email")
    @Operation(summary = "set email")
    public RegisterRs setNewEmail(@RequestBody @Parameter EmailRq emailRq) {return personService.setNewEmail(emailRq);}

    @OnlineStatusUpdatable
    @PutMapping("/password/recovery")
    @Operation(summary = "user password recovery")
    public void passwordChangeConfirm(@RequestHeader @Parameter String authorization) {
        emailService.passwordChangeConfirm(authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/password/reset")
    @Operation(summary = "user password reset")
    public RegisterRs resetPassword(@RequestHeader @Parameter String authorization,
                                       @RequestBody @Parameter PasswordSetRq passwordSetRq) {
        return personService.resetPassword(authorization, passwordSetRq);
    }

    @OnlineStatusUpdatable
    @PutMapping("/password/set")
    @Operation(summary = "set user password")
    public RegisterRs setNewPassword(@RequestHeader @Parameter String authorization,
                                        @RequestBody @Parameter PasswordSetRq passwordSetRq) {
        return personService.resetPassword(authorization, passwordSetRq);
    }

    @PostMapping("/register")
    @Operation(summary = "user registration")
    public RegisterRs register(@Valid @RequestBody @Parameter RegisterRq regRequest) {
        return accountService.getRegisterData(regRequest);
    }

    @OnlineStatusUpdatable
    @GetMapping("/notifications")
    @Operation(summary = "get user's notifications properties")
    public CommonRs<List<PersonSettingsRs>> notifications(@RequestHeader @Parameter String authorization){
        return personService.getPersonSettings(authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/notifications")
    @Operation(summary = "edit notifications properties")
    public CommonRs<ComplexRs> saveSettings(@RequestHeader @Parameter String authorization,
                                            @RequestBody @Parameter PersonSettingsRq personSettingsRq) {
        return personService.setSetting(authorization, personSettingsRq);
    }
}
