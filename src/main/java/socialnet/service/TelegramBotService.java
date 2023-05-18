package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import socialnet.api.request.TgApiRequest;
import socialnet.api.response.TgApiRs;
import socialnet.api.response.TgNotificationFromRs;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.repository.TelegramBotRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.MailSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final TelegramBotRepository telegramBotRepository;
    private final PersonRepository personRepository;
    private final MailSender mailSender;
    private final JwtUtils jwtUtils;

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

        if (command.equals("/token")) {
            return handleTokenCommand(request);
        }

        if (command.equals("/notificate")) {
            return handleNotificateCommand(request);
        }

        return makeResponse("fail", "Неизвестная команда", null);
    }

    private TgApiRs handleNotificateCommand(TgApiRequest request) {
        Map<String, List<TgNotificationFromRs>> notifications = telegramBotRepository.getNotifications(request.getData());

        if (notifications.isEmpty()) {
            return makeResponse("fail", "No data", null);
        }

        return makeResponse("ok", null, new JSONObject(notifications).toString());
    }

    private TgApiRs handleTokenCommand(TgApiRequest request) {
        Person person = personRepository.findByTelegramId(request.getId());

        if (person == null) {
            return makeResponse("fail", "Error", null);
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", jwtUtils.generateJwtToken(person.getEmail()));
        map.put("id", String.valueOf(request.getId()));
        JSONObject jo = new JSONObject(map);

        return makeResponse("ok", null, jo.toString());
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
                "http://81.177.6.228:8086/api/v1/tg?id=" + request.getId()
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

        String token = jwtUtils.generateJwtToken(person.getEmail());
        String fullName = "[" + person.getFirstName() + " " + person.getLastName() + "]";

        Map<String, String> map = new HashMap<>();
        map.put("name", fullName);
        map.put("token", token);
        map.put("id", String.valueOf(request.getId()));
        map.put("userId", person.getId().toString());
        JSONObject jo = new JSONObject(map);

        return makeResponse("ok", null, jo.toString());
    }

    private TgApiRs makeResponse(String status, String error, String data) {
        return TgApiRs.builder().status(status).error(error).data(data).build();
    }
}
