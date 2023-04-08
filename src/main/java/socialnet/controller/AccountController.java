package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.RegisterRq;
import socialnet.api.response.RegisterRs;
import socialnet.service.AccountService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public RegisterRs register(@Valid @RequestBody RegisterRq regRequest) {
        return accountService.getRegisterData(regRequest);
    }
    @GetMapping("notifications")
    public Object notifications(@RequestHeader String authorization){
        return  new Object();
    }
}
