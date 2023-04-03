package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LoginRq;
import socialnet.service.login.LoginServiceImpl;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

    private final LoginServiceImpl loginServer;

    @PostMapping("/auth/login")
    public Object login(@RequestBody LoginRq loginRq) {
        return loginServer.getLogin(loginRq);
    }

    @GetMapping("users/me")
    public Object Me(@RequestHeader(name = "authorization") String authorization) {
        return loginServer.getMe(authorization);
    }

    @PostMapping("/auth/logout")
    public Object logout(@RequestHeader(name = "authorization") String authorization) {
        return loginServer.getLogout(authorization);
    }
}
