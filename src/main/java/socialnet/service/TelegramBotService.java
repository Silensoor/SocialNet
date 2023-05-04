package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.request.TgApiRequest;
import socialnet.api.response.TgApiRs;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.repository.TelegramBotRepository;
import socialnet.utils.MailSender;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final TelegramBotRepository telegramBotRepository;
    private final PersonRepository personRepository;
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

        if (command.equals("/killyourself")) {
            return handleKillyourselfCommand(request);
        }

        return makeResponse("fail", "Неизвестная команда", null);
    }

    private TgApiRs handleKillyourselfCommand(TgApiRequest request) {
        boolean isUnregistered = telegramBotRepository.unregister(request.getId());

        if (isUnregistered) {
            return makeResponse("ok", null, "Бывай!");
        }

        return makeResponse("fail", "Какая-то фигня, так что сиди пока и не рыпайся.", null);
    }

    private TgApiRs handleRegisterCommand(TgApiRequest request) {
        try {
            mailSender.send(
                request.getData(),
                "Подтверждение регистрации",
                "http://localhost:8086/api/v1/tg?id=" + request.getId()
                    + "&email=" + request.getData()
                    + "&cmd=" + request.getCommand().substring(1)
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return makeResponse("ok", null, "Письмо с подтверждением выслано на указанную почту. " +
            "Войдите в почту и перейдите по ссылке, затем нажмите 'Подтвердить регистрацию'");
    }

    private TgApiRs handleLoginCommand(TgApiRequest request) {
        Person person = personRepository.findByTelegramId(request.getId());

        if (person == null) {
            return makeResponse("fail", "Укажите свою почту:", null);
        }

        return makeResponse("ok", null, "[" + person.getFirstName() + " " + person.getLastName() + "]");
    }

    private TgApiRs makeResponse(String status, String error, String data) {
        return TgApiRs.builder().status(status).error(error).data(data).build();
    }
}
