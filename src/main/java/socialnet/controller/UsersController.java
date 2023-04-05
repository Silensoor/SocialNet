package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.service.LoginService;
import socialnet.service.UserByIdService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final LoginService loginService;
    private final UserByIdService userByIdService;

    @GetMapping("/me")
    public Object Me(@RequestHeader(name = "authorization") String authorization) {
        return loginService.getMe(authorization);
    }

    @GetMapping("/{id}")
    public Object Id(@RequestHeader(name = "authorization") String authorization, @PathVariable(name = "id") Integer id) {
        return userByIdService.getUserById(authorization, id);
    }
}
