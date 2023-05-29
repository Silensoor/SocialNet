package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.DialogUserShortListDto;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.DialogRs;
import socialnet.api.response.MessageRs;
import socialnet.aspects.OnlineStatusUpdatable;
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
    @ApiOperation(value = "recover comment by id")
    public CommonRs<List<DialogRs>> getDialogs(@RequestHeader String authorization) {
        return dialogsService.getDialogs(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs/unreaded", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "get count of unread messages")
    public CommonRs<ComplexRs> getUnreadedMessages(@RequestHeader String authorization) {
        return dialogsService.getUnreadedMessages(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs/{dialogId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "get messages from dialog")
    public CommonRs<List<MessageRs>> getMessagesFromDialog(
            @RequestHeader String authorization,
            @PathVariable("dialogId") Long dialogId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage) {
        return dialogsService.getMessagesFromDialog(authorization, dialogId, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PutMapping(value = "/dialogs/{dialogId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "read messages in dialog")
    public CommonRs<ComplexRs> readMessagesInDialog(@RequestHeader String authorization,
                                                    @PathVariable("dialogId") Long dialogId) {
        return dialogsService.readMessagesInDialog(dialogId);
    }

    @OnlineStatusUpdatable
    @PostMapping(value = "/dialogs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "start dialog with user")
    public CommonRs<ComplexRs> startDialog(@RequestHeader String authorization,
                                           @RequestBody DialogUserShortListDto dialogUserShortListDto) {
        return dialogsService.registerDialog(authorization, dialogUserShortListDto);
    }
}