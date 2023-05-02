package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.TgApiRequest;
import socialnet.service.TelegramBotService;

@RestController
@RequestMapping("/api/v1/tg")
@RequiredArgsConstructor
public class TelegramBotController {
    private final TelegramBotService telegramBotService;

    @PostMapping
    public void execCommand(@RequestBody TgApiRequest request) {
        telegramBotService.execCommand(request);
    }

    @GetMapping
    public void register(@RequestParam long id, @RequestParam String email, @RequestParam String cmd) {
        telegramBotService.register(id, email, cmd);
    }
}
