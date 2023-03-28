package socialnet.controller;

import liquibase.repackaged.org.apache.commons.lang3.tuple.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import socialnet.api.ErrorRs;
import socialnet.dto.CommonRs;
import socialnet.dto.PostRs;
import socialnet.service.PostService;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class NewsFeedController {

    private final PostService postService;

    @GetMapping(value = "/api/v1/feeds", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonRs<List<PostRs>> getNewsFeed(@RequestParam String authorization,
                                              @RequestParam(defaultValue = "0") Integer offset,
                                              @RequestParam(defaultValue = "20") Integer perPage) {

        Pair<Integer, List<PostRs>> pair = postService.getAllPosts(offset, perPage);
        CommonRs<List<PostRs>> result = new CommonRs<>();
        result.setOffset(offset);
        result.setPerPage(perPage);
        result.setTimestamp((int) System.currentTimeMillis());
        result.setItemPerPage(perPage);
        result.setTotal(pair.getLeft());
        result.setData(pair.getRight());

        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRs handleJsonMappingException(Exception exception) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("/api/v1/feeds returned exception");
        errorRs.setErrorDescription(exception.getLocalizedMessage());
        errorRs.setTimestamp(new Timestamp(System.currentTimeMillis()));

        return errorRs;
    }
}
