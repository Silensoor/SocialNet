package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socialnet.api.request.DialogUserShortListDto;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        List<Dialog> ownDialogs = dialogsRepository.findByAuthorId(person.getId());
        List<Dialog> companionDialogs = dialogsRepository.findByRecipientId(person.getId());
        List<Dialog> dialogsModelAll = new ArrayList<>();
        dialogsModelAll.addAll(ownDialogs);
        dialogsModelAll.addAll(companionDialogs);
        List<DialogRs> dialogList = new ArrayList<>();
        for (Dialog dialog : dialogsModelAll) {
            DialogRs dialogRs = DialogMapper.INSTANCE.toDTO(dialog);
            Message lastMessage = messageRepository.findById(dialog.getLastMessageId());
            if (lastMessage == null) {
                continue;
            }
            MessageRs messageRs = MessageMapper.INSTANCE.toDTO(lastMessage);
            PersonRs recipient;
            if (Objects.equals(person.getId(), dialog.getFirstPersonId())) {
                recipient = PersonMapper.INSTANCE.toDTO(personRepository.findById(dialog.getSecondPersonId()));
            } else {
                recipient = PersonMapper.INSTANCE.toDTO(personRepository.findById(dialog.getFirstPersonId()));
            }
            messageRs.setRecipient(recipient);
            messageRs.setIsSentByMe(lastMessage.getAuthorId().equals(person.getId()));
            dialogRs.setLastMessage(messageRs);
            dialogRs.setReadStatus(lastMessage.getReadStatus());
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
        long count = messageRepository.findCountByPersonIdAndReadStatus(person.getId(), MessageReadStatus.UNREAD.name());

        ComplexRs complexRs = new ComplexRs();
        complexRs.setCount(count);

        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(complexRs);
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<List<MessageRs>> getMessagesFromDialog(String token, Long dialogId, Integer offset, Integer perPage) {
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.getPersonByEmail(userEmail);
        List<Message> messagesModel = messageRepository.findByDialogId(dialogId, offset, perPage);
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
        result.setTotal(messageRepository.countByDialogId(dialogId));

        return result;
    }

    public CommonRs<ComplexRs> readMessagesInDialog(Long dialogId) {
        int count = messageRepository.updateReadStatusByDialogId(dialogId, MessageReadStatus.READ.name(), MessageReadStatus.UNREAD.name());
        ComplexRs complexRs = ComplexRs.builder().count((long) count).build();
        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(complexRs);
        result.setTimestamp(System.currentTimeMillis());

        return result;
    }

    public CommonRs<ComplexRs> registerDialog(String token, DialogUserShortListDto dialogUserShortListDto) {
        ComplexRs complexRs = new ComplexRs();
        String userEmail = jwtUtils.getUserEmail(token);
        Person person = personRepository.getPersonByEmail(userEmail);
        Dialog dialog = dialogsRepository.findByAuthorAndRecipient(person.getId(), dialogUserShortListDto.getUserIds().get(0));
        long savedDialogId;
        if (dialog == null) {
            dialog = Dialog.builder()
                    .firstPersonId(person.getId())
                    .secondPersonId(dialogUserShortListDto.getUserIds().get(0))
                    .lastActiveTime(new Timestamp(System.currentTimeMillis()))
                    .build();
            savedDialogId = dialogsRepository.save(dialog);
            Message message = Message.builder()
                    .isDeleted(true)
                    .dialogId(savedDialogId)
                    .authorId(dialog.getFirstPersonId())
                    .recipientId(dialog.getSecondPersonId())
                    .build();
            long savedMessageId = messageRepository.save(message);
            dialogsRepository.updateField(savedDialogId, "last_message_id", savedMessageId);
        } else {
            savedDialogId = dialog.getId();
        }
        complexRs.setId((int) savedDialogId);
        CommonRs<ComplexRs> commonRs = new CommonRs<>();
        commonRs.setData(complexRs);

        return commonRs;
    }
}
