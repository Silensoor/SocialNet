package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import socialnet.api.request.TgApiRequest;
import socialnet.repository.TelegramBotRepository;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final TelegramBotRepository telegramBotRepository;

    public void register(long telegramId, String email, String cmd) {
        telegramBotRepository.register(telegramId, email, cmd);
    }

    public void execCommand(TgApiRequest request) {
        System.out.println("Telegram ID: " + request.getId());
        System.out.println("Command: " + request.getCommand());
    }
}
