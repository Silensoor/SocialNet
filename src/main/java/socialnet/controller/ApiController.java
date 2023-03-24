package socialnet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socialnet.dto.LoginRq;
import socialnet.dto.UserRq;
import socialnet.service.login.LoginService;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final LoginService loginServer;

    public ApiController(LoginService loginServer) {
        this.loginServer = loginServer;
    }

    @GetMapping("/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRq loginRq) throws AuthenticationException {
        return ResponseEntity.ok(loginServer.getLogin(loginRq));
    }
}
