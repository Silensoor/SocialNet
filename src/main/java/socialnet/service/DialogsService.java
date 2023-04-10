package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socialnet.api.response.*;
import socialnet.mappers.DialogMapper;
import socialnet.mappers.MessageMapper;
import socialnet.mappers.PersonMapper;
import socialnet.model.Dialog;
import socialnet.model.Message;
import socialnet.model.Person;
import socialnet.repository.DialogsRepository;
import socialnet.repository.MessageRepository;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DialogsService {

    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final DialogsRepository dialogsRepository;
    private final MessageRepository messageRepository;

    public CommonRs<List<DialogRs>> getDialogs(String token) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.getPersonByEmail(userEmail);
        List<Dialog> dialogsModel = dialogsRepository.findByAuthorId(person.getId());
        List<DialogRs> dialogList = new ArrayList<>();
        for (Dialog dialog : dialogsModel) {
            DialogRs dialogRs = DialogMapper.INSTANCE.toDTO(dialog);
            Message lastMessage = messageRepository.findByDialogId(dialog.getId());
            MessageRs messageRs = MessageMapper.INSTANCE.toDTO(lastMessage);
            dialogRs.setLastMessage(messageRs);
            dialogRs.setReadStatus(lastMessage.getReadStatus());
            PersonRs recipient = PersonMapper.INSTANCE.toDTO(personRepository.findById(lastMessage.getRecipientId()));
            messageRs.setRecipient(recipient);
            dialogRs.setUnreadCount(messageRepository.findCountByDialogIdAndReadStatus(dialog.getId(), "UNREAD"));
            dialogList.add(dialogRs);
        }

        CommonRs<List<DialogRs>> result = new CommonRs<>();
        result.setData(dialogList);
        result.setTotal((long) dialogList.size());
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<ComplexRs> getUnreadDialogs(String token) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.getPersonByEmail(userEmail);
        CommonRs<ComplexRs> result = new CommonRs<>();
        ComplexRs complexRs = new ComplexRs();
        Message message = messageRepository.findByAuthorId(person.getId());
        complexRs.setMessageId(message.getId());
        long count =messageRepository.findCountByAuthorIdAndReadStatus(person.getId(), "UNREAD");
        complexRs.setCount(count);
        complexRs.setMessage(message.getMessageText());
        result.setData(complexRs);
        result.setTotal(count);
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }
}
