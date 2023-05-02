package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.TgApiRequest;

@RestController
@RequestMapping("/api/v1/tg")
@RequiredArgsConstructor
public class TelegramBotController {
    @PostMapping
    public void execCommand(@RequestBody TgApiRequest request) {
        System.out.println("Telegram ID: " + request.getId());
        System.out.println("Command: " + request.getCommand());
    }
}
