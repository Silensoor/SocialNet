package socialnet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.DialogRs;
import socialnet.api.response.MessageRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.exception.DialogsException;
import socialnet.service.DialogsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "dialogs-controller", description = "Get dialogs, start dialog, get read and unread messages")
public class DialogsController {

    private final DialogsService dialogsService;

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonRs<List<DialogRs>> getDialogs(@RequestHeader String authorization) {
        CommonRs<List<DialogRs>> dialogs;
        try {
            dialogs = dialogsService.getDialogs(authorization);
        } catch (Exception e) {
            log.error("Error in DialogsService::getDialogs", e);
            throw new DialogsException(e);
        }
        return dialogs;
    }

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs/unreaded", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonRs<ComplexRs> getUnreadedMessages(@RequestHeader String authorization) {
        return dialogsService.getUnreadedMessages(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs/{dialogId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonRs<List<MessageRs>> getMessagesFromDialog(
        @RequestHeader String authorization,
        @PathVariable("dialogId") Long dialogId,
        @RequestParam(defaultValue = "0") Integer offset,
        @RequestParam(defaultValue = "20") Integer perPage)
    {
        log.debug("token = {}", authorization);
        return dialogsService.getMessagesFromDialog(authorization, dialogId, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PutMapping(value = "/dialogs/{dialogId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonRs<ComplexRs> readMessagesInDialog(@RequestHeader String authorization,
                                                    @PathVariable("dialogId") Long dialogId) {
        log.debug("token = {}", authorization);
        return dialogsService.readMessagesInDialog(dialogId);
    }
}