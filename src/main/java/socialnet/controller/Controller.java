package socialnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.dto.LoginRq;
import socialnet.service.login.LoginServiceImpl;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    private final LoginServiceImpl loginServer;

    public Controller(LoginServiceImpl loginServer) {
        this.loginServer = loginServer;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRq loginRq) {
        return ResponseEntity.ok(loginServer.getLogin(loginRq));
    }

    @GetMapping("users/me")
    public ResponseEntity<?> Me(@RequestHeader(name = "authorization") String authorization) {
        return ResponseEntity.ok(loginServer.getMe(authorization));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "authorization") String authorization) {
        return ResponseEntity.ok(loginServer.getLogout(authorization));
    }
}
