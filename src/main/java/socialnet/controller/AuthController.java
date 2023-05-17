package socialnet.controller;

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
public class AuthController {
    private final CaptchaService captchaService;
    private final PersonService personService;

    @GetMapping("/captcha")
    public CaptchaRs captcha() {
        return captchaService.getCaptchaData();
    }

    @PostMapping("/login")
    public CommonRs<PersonRs> login(@RequestBody LoginRq loginRq) {
        return personService.getLogin(loginRq);
    }

    @OnlineStatusUpdatable
    @PostMapping("/logout")
    public CommonRs<ComplexRs> logout(@RequestHeader(name = "authorization") String authorization) {
        return personService.getLogout(authorization);
    }
}
