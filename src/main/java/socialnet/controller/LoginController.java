package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import socialnet.dto.LoginRq;
import socialnet.dto.LoginRs;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.LoginService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final JwtUtils jwtUtils;
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(LoginRq loginRq) {

        System.err.println(new BCryptPasswordEncoder().encode(loginRq.getPassword()));

        return ResponseEntity.ok(loginService.login(loginRq));
    }

    @GetMapping("users/me")
    public ResponseEntity<?> Me(@RequestParam(name = "jwt") String jwt) {
        LoginRq loginRq = new LoginRq();
        String userNameFromJwtToken = jwtUtils.getUserNameFromJwtToken(jwt);
        loginRq.setEmail(userNameFromJwtToken);
        loginRq.setPassword("dkvf198855");

        LoginRs login = loginService.login(loginRq);

        return ResponseEntity.ok(login);
    }
}

