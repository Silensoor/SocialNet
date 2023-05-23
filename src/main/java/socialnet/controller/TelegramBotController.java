package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.TgApiRequest;
import socialnet.api.response.TgApiRs;
import socialnet.service.TelegramBotService;

@RestController
@RequestMapping("/api/v1/tg")
@RequiredArgsConstructor
@Tag(name = "telegram-bot-controller", description = "Telegram connection")
public class TelegramBotController {
    private final TelegramBotService telegramBotService;

    @PostMapping
    @ApiOperation(value = "telegramBot execute command")
    public ResponseEntity<TgApiRs> execCommand(@RequestBody @Parameter TgApiRequest request) {
        return ResponseEntity.ok(telegramBotService.execCommand(request));
    }

    @GetMapping
    @ApiOperation(value = "telegramBot register user")
    public ResponseEntity<TgApiRs> register(@RequestParam @Parameter long id, @RequestParam @Parameter String email,
                                            @RequestParam @Parameter String cmd) {
        return ResponseEntity.ok(telegramBotService.register(id, email, cmd));
    }
}
