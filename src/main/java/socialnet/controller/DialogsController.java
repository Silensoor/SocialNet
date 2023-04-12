package socialnet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.DialogRs;
import socialnet.exception.DialogsException;
import socialnet.service.DialogsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DialogsController {

    private final DialogsService dialogsService;

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

    @GetMapping(value = "/dialogs/unreaded", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonRs<ComplexRs> getUnreadedMessages(@RequestHeader String authorization) {
        return dialogsService.getUnreadDialogs(authorization);
    }
}