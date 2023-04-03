package socialnet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PostRs;
import socialnet.service.PostService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class NewsController {

    private final PostService postService;

    @GetMapping(value = "/feeds", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonRs<List<PostRs>> getNewsFeed(@RequestHeader String authorization,
                                              @RequestParam(defaultValue = "0") Integer offset,
                                              @RequestParam(defaultValue = "20") Integer perPage) {

        log.debug("landsreyk::Authorization token: " + authorization);
        return postService.getAllPosts(offset, perPage);
    }
}
