package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LoginRq;
import socialnet.api.response.CaptchaRs;
import socialnet.service.AuthService;
import socialnet.service.LoginService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final LoginService loginService;

    @GetMapping("/captcha")
    public CaptchaRs captcha() {
        return authService.getCaptchaData();
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRq loginRq) {
        return loginService.getLogin(loginRq);
    }

    @PostMapping("/logout")
    public Object logout(@RequestHeader(name = "authorization") String authorization) {
        return loginService.getLogout(authorization);
    }
}
