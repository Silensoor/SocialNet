package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.service.PersonService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final PersonService personService;

    @GetMapping("/me")
    public Object Me(@RequestHeader(name = "authorization") String authorization) {
        return personService.getMe(authorization);
    }

    @GetMapping("/{id}")
    public Object Id(@RequestHeader(name = "authorization") String authorization, @PathVariable(name = "id") Integer id) {
        return personService.getUserById(authorization, id);
    }
}
