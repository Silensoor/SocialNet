package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.account.RegisterRq;
import socialnet.api.account.RegisterRs;
import socialnet.service.AccountService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<RegisterRs> register(@Valid @RequestBody RegisterRq regRequest) {
        return new ResponseEntity<>(accountService.getRegisterData(regRequest), HttpStatus.OK);
    }
}
