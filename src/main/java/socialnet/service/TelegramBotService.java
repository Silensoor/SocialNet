package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.TgApiRequest;
import socialnet.repository.TelegramBotRepository;
import socialnet.utils.MailSender;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final TelegramBotRepository telegramBotRepository;
    private final MailSender mailSender;

    public void register(long telegramId, String email, String cmd) {
        telegramBotRepository.register(telegramId, email, cmd);
    }

    public void execCommand(TgApiRequest request) {
        System.out.println("Telegram ID: " + request.getId());
        System.out.println("Command: " + request.getCommand());

        try {
            mailSender.send("jonnysereb@gmail.com", "Subject", "http://localhost:8086/api/v1/tg?id=" + request.getId() + "&cmd=register");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
