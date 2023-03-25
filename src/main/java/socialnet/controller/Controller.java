package socialnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.dto.LoginRq;
import socialnet.service.login.LoginServiceImpl;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    private final LoginServiceImpl loginServer;

    public Controller(LoginServiceImpl loginServer) {
        this.loginServer = loginServer;
    }

    @GetMapping("/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRq loginRq) throws AuthenticationException {
        return ResponseEntity.ok(loginServer.getLogin(loginRq));
    }

    @GetMapping("users/me")
    public ResponseEntity<?> Me() {
        return ResponseEntity.ok(loginServer.getMe());
    }
}
