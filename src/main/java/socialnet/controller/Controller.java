package socialnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import socialnet.dto.LoginRq;
import socialnet.dto.login.LoginRs;
import socialnet.service.login.LoginServiceImpl;

import javax.naming.Name;

@RestController
@RequestMapping("/api/v1")
public class Controller {

    private final LoginServiceImpl loginServer;

    public Controller(LoginServiceImpl loginServer) {
        this.loginServer = loginServer;
    }

    @GetMapping("/auth/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRq loginRq) {
        return ResponseEntity.ok(loginServer.getLogin(loginRq));
    }

//    @GetMapping("users/me")
//    public ResponseEntity<?> Me(@RequestParam(name = "jwt") String jwt) {
//        return ResponseEntity.ok(loginServer.getMe(jwt));
//    }
}
