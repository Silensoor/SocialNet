package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.request.TgApiRequest;
import socialnet.api.response.TgApiRs;
import socialnet.repository.TelegramBotRepository;
import socialnet.utils.MailSender;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final TelegramBotRepository telegramBotRepository;
    private final MailSender mailSender;

    public TgApiRs register(long telegramId, String email, String cmd) {
        boolean isRegister = telegramBotRepository.register(telegramId, email, cmd);

        TgApiRs response = new TgApiRs();

        if (!isRegister) {
            response.setStatus("fail");
            response.setError("Данный email уже зарегистрирован");

            return response;
        }

        response.setStatus("ok");
        response.setData("Письмо с подтверждением регистрации выслано на почту. " +
                "Перейдите по ссылке");

        return response;
    }

    public TgApiRs execCommand(TgApiRequest request) {
        String command = request.getCommand();

        if (command.equals("/login")) {
            return handleLoginCommand(request);
        }

        TgApiRs response = new TgApiRs();
        response.setStatus("fail");
        response.setError(null);
        response.setData(null);

        return response;
    }

    private TgApiRs handleLoginCommand(TgApiRequest request) {
        try {
            mailSender.send("jonnysereb@gmail.com", "Subject", "http://localhost:8086/api/v1/tg?id=" + request.getId() + "&cmd=register");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        TgApiRs response = new TgApiRs();
        response.setStatus("ok");
        response.setError(null);
        response.setData(null);

        return response;
    }
}
