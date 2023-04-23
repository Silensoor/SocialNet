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
import socialnet.model.enums.MessageReadStatus;
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
        List<Dialog> dialogsModelAuthor = dialogsRepository.findByAuthorId(person.getId());
        List<Dialog> dialogsModelRecipient = dialogsRepository.findByRecipientId(person.getId());
        List<Dialog> dialogsModelAll = new ArrayList<>();
        dialogsModelAll.addAll(dialogsModelAuthor);
        dialogsModelAll.addAll(dialogsModelRecipient);
        List<DialogRs> dialogList = new ArrayList<>();
        for (Dialog dialog : dialogsModelAll) {
            DialogRs dialogRs = DialogMapper.INSTANCE.toDTO(dialog);
            Message lastMessage = messageRepository.findLastMessageByDialogId(dialog.getId());
            MessageRs messageRs = MessageMapper.INSTANCE.toDTO(lastMessage);
            dialogRs.setLastMessage(messageRs);
            dialogRs.setReadStatus(lastMessage.getReadStatus());
            PersonRs recipient = PersonMapper.INSTANCE.toDTO(personRepository.findById(lastMessage.getRecipientId()));
            messageRs.setRecipient(recipient);
            dialogRs.setUnreadCount(messageRepository.findCountByDialogIdAndReadStatus(dialog.getId(), MessageReadStatus.UNREAD.name()));
            dialogList.add(dialogRs);
        }

        CommonRs<List<DialogRs>> result = new CommonRs<>();
        result.setData(dialogList);
        result.setTotal((long) dialogList.size());
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<ComplexRs> getUnreadedMessages(String token) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.getPersonByEmail(userEmail);
        long count = messageRepository.findCountByAuthorIdAndReadStatus(person.getId(), MessageReadStatus.UNREAD.name());

        ComplexRs complexRs = new ComplexRs();
        complexRs.setCount(count);

        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(complexRs);
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<List<MessageRs>> getMessagesFromDialog(String token, Long dialogId, Integer itemPerPage) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.getPersonByEmail(userEmail);
        List<Message> messagesModel = messageRepository.findByDialogId(dialogId, itemPerPage);
        List<MessageRs> messagesDto = new ArrayList<>();
        for (Message messageModel : messagesModel) {
            MessageRs messageDto = MessageMapper.INSTANCE.toDTO(messageModel);
            Person recipientModel = personRepository.findById(messageModel.getRecipientId());
            PersonRs recipientDto = PersonMapper.INSTANCE.toDTO(recipientModel);
            messageDto.setRecipient(recipientDto);
            messageDto.setIsSentByMe(messageModel.getAuthorId().equals(person.getId()));
            messagesDto.add(messageDto);
        }
        CommonRs<List<MessageRs>> result = new CommonRs<>();
        result.setData(messagesDto);

        return result;
    }

    public CommonRs<ComplexRs> readMessagesInDialog(Long dialogId) {
        int count = messageRepository.updateReadStatusByDialogId(dialogId, "READ");
        ComplexRs complexRs = ComplexRs.builder().count((long) count).build();
        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(complexRs);
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }
}
