package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socialnet.api.request.MessageWs;
import socialnet.model.Dialog;
import socialnet.model.Message;
import socialnet.repository.DialogsRepository;
import socialnet.repository.MessageRepository;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final DialogsRepository dialogsRepository;

    public MessageWs processMessage(Long dialogId, MessageWs message) {
        Dialog dialog = dialogsRepository.findByDialogId(dialogId);
        message.setRecipientId(dialog.getSecondPersonId());
        Message messageModel = Message.builder()
                .isDeleted(false)
                .messageText(message.getMessageText())
                .readStatus(message.getReadStatus())
                .time(new Timestamp(message.getTime()))
                .dialogId(message.getDialogId())
                .authorId(message.getAuthorId())
                .recipientId(message.getRecipientId())
                .build();
        messageRepository.save(messageModel);

        return message;
    }
}
