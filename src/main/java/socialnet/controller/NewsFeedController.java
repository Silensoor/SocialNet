package socialnet.controller;

import liquibase.repackaged.org.apache.commons.lang3.tuple.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.ErrorRs;
import socialnet.dto.CommonRs;
import socialnet.dto.PostRs;
import socialnet.service.PostService;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class NewsFeedController {

    private final PostService postService;

    @GetMapping(value = "/api/v1/feeds", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonRs<List<PostRs>>> getNewsFeed(@RequestHeader String authorization,
                                                              @RequestParam(defaultValue = "0") Integer offset,
                                                              @RequestParam(defaultValue = "20") Integer perPage) {

        log.debug("landsreyk::Authorization token: " + authorization);
        Pair<Integer, List<PostRs>> pair = postService.getAllPosts(offset, perPage);
        CommonRs<List<PostRs>> result = new CommonRs<>();
        result.setOffset(offset);
        result.setPerPage(perPage);
        result.setTimestamp((int) System.currentTimeMillis());
        result.setItemPerPage(perPage);
        result.setTotal(pair.getLeft());
        result.setData(pair.getRight());

        return ResponseEntity.ok().body(result);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorRs> handleJsonMappingException(Exception exception) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("/api/v1/feeds returned exception");
        errorRs.setErrorDescription(exception.getLocalizedMessage());
        errorRs.setTimestamp(new Timestamp(System.currentTimeMillis()));

        return ResponseEntity.badRequest().body(errorRs);
    }
}
