package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.TgApiRequest;
import socialnet.api.response.TgApiRs;
import socialnet.service.TelegramBotService;

@RestController
@RequestMapping("/api/v1/tg")
@RequiredArgsConstructor
public class TelegramBotController {
    private final TelegramBotService telegramBotService;

    @PostMapping
    public ResponseEntity<TgApiRs> execCommand(@RequestBody TgApiRequest request) {
        return ResponseEntity.ok(telegramBotService.execCommand(request));
    }

    @GetMapping
    public ResponseEntity<TgApiRs> register(@RequestParam long id, @RequestParam String email, @RequestParam String cmd) {
        return ResponseEntity.ok(telegramBotService.register(id, email, cmd));
    }
}
