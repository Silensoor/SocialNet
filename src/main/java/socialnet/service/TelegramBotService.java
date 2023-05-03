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

        if (!isRegister) {
            return makeResponse("fail", "Вы уже зарегистрированы", null);
        }

        return makeResponse("ok", null, "Регистрация прошла успешно");
    }

    public TgApiRs execCommand(TgApiRequest request) {
        String command = request.getCommand();

        if (command.equals("/register")) {
            return handleRegisterCommand(request);
        }

        if (command.equals("/login")) {
            return handleLoginCommand(request);
        }

        return makeResponse("fail", "Неизвестная команда", null);
    }

    private TgApiRs handleRegisterCommand(TgApiRequest request) {
        try {
            mailSender.send(
                "jonnysereb@gmail.com",
                "Подтверждение регистрации",
                "http://localhost:8086/api/v1/tg?id=" + request.getId()
                    + "&email=" + request.getData()
                    + "&cmd=" + request.getCommand()
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return makeResponse("ok", null, "Письмо выслано на указанную почту. Подтвердите регистрацию и можно входить.");
    }

    private TgApiRs handleLoginCommand(TgApiRequest request) {
        TgApiRs response = new TgApiRs();
        response.setStatus("ok");
        response.setError(null);
        response.setData(null);

        return response;
    }

    private TgApiRs makeResponse(String status, String error, String data) {
        TgApiRs response = new TgApiRs();
        response.setStatus(status);
        response.setError(error);
        response.setData(data);

        return response;
    }
}
