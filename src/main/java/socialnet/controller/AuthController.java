package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LoginRq;
import socialnet.api.response.CaptchaRs;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.PersonRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.CaptchaService;
import socialnet.service.PersonService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "auth-controller", description = "Working with captcha, login and logout")
public class AuthController {
    private final CaptchaService captchaService;
    private final PersonService personService;

    @GetMapping("/captcha")
    @ApiOperation(value = "get captcha secret code and image url")
    public CaptchaRs captcha() {
        return captchaService.getCaptchaData();
    }

    @PostMapping("/login")
    @ApiOperation(value = "login by email and password")
    public CommonRs<PersonRs> login(@RequestBody LoginRq loginRq) {
        return personService.getLogin(loginRq);
    }

    @OnlineStatusUpdatable
    @PostMapping("/logout")
    @ApiOperation(value = "logout current user")
    public CommonRs<ComplexRs> logout(@RequestHeader(name = "authorization") String authorization) {
        return personService.getLogout(authorization);
    }
}
