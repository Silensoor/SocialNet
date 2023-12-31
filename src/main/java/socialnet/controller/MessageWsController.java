package socialnet.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import socialnet.api.request.MessageCommonWs;
import socialnet.api.request.MessageTypingWs;
import socialnet.api.request.MessageWs;
import socialnet.service.MessageService;

@Controller
@RequiredArgsConstructor
@Api(tags = "message-controller")
public class MessageWsController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/dialogs/send_message")
    public void sendMessage(@Header("dialog_id") Long dialogId, @Payload MessageWs message) {
        message = messageService.processMessage(dialogId, message);
        messagingTemplate.convertAndSendToUser(dialogId.toString(), "/queue/messages", message);
    }

    @MessageMapping("/dialogs/start_typing")
    public void startTyping(@Header("dialog_id") Long dialogId, @Payload MessageTypingWs messageTypingWs) {
        messagingTemplate.convertAndSendToUser(dialogId.toString(), "/queue/messages", messageTypingWs);
    }

    @MessageMapping("/dialogs/stop_typing")
    public void stopTyping(@Header("dialog_id") Long dialogId, @Payload MessageTypingWs messageTypingWs) {
        messagingTemplate.convertAndSendToUser(dialogId.toString(), "/queue/messages", messageTypingWs);
    }

    @MessageMapping("/dialogs/edit_message")
    public void editMessage(@Payload MessageCommonWs messageCommonWs) {
        messageService.editMessage(messageCommonWs);
        messagingTemplate.convertAndSendToUser(
                messageCommonWs.getDialogId().toString(), "/queue/messages", messageCommonWs);
    }

    @MessageMapping("/dialogs/delete_messages")
    public void deleteMessages(@Payload MessageCommonWs messageCommonWs) {
        messageService.deleteMessages(messageCommonWs);
        messagingTemplate.convertAndSendToUser(
                messageCommonWs.getDialogId().toString(), "/queue/messages", messageCommonWs);
    }

    @MessageMapping("/dialogs/recover_message")
    public void recoverMessage(@Payload MessageCommonWs messageCommonWs) {
        messageService.recoverMessages(messageCommonWs);
        messagingTemplate.convertAndSendToUser(
                messageCommonWs.getDialogId().toString(), "/queue/messages", messageCommonWs);
    }

    @MessageMapping("/dialogs/close_dialog")
    public void closeDialog(@Payload MessageCommonWs messageCommonWs) {
        messagingTemplate.convertAndSendToUser(
                messageCommonWs.getDialogId().toString(), "/queue/messages", messageCommonWs);
    }

}
