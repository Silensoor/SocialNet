package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import socialnet.service.PostsService;

@Controller
@RequiredArgsConstructor
public class PostsController {
    private final PostsService postsService;

    @GetMapping("/api/v1/feeds")
    public ResponseEntity<String> getFeeds(@RequestParam int offset, @RequestParam int perPage) {
        System.out.println("!!!");
        postsService.getFeeds(offset, perPage);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
