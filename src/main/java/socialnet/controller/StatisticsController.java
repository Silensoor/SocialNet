package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.RegionStatisticsRs;
import socialnet.service.StatisticsService;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/city")
    public Integer getAllCities()
    {
        return statisticsService.getAllCities();
    }

    @GetMapping("/city/all")
    public RegionStatisticsRs[] getCitiesUsers()
    {
        return statisticsService.getCitiesUsers();
    }

    @GetMapping("/comment/post")
    public ResponseEntity<?> getCommentsByPost(@RequestParam(required = false, defaultValue = "0") Integer postId)
    {
       return statisticsService.getCommentsByPost(postId);
    }

    @GetMapping("/country")
    public Integer getCountry()
    {
        return statisticsService.getCountry();
    }

    @GetMapping("/country/all")
    public RegionStatisticsRs[] getCountryUsers()
    {
        return statisticsService.getCountryUsers();
    }

    @GetMapping("/dialog")
    public Integer getDialog()
    {
        return statisticsService.getDialog();
    }

    @GetMapping("/dialog/user")
    public ResponseEntity<?> getDialogsUser(@RequestParam(required = false, defaultValue = "0") Integer userId)
    {
        return statisticsService.getDialogsUser(userId);
    }

    @GetMapping("/like")
    public Integer getAllLike()
    {
        return statisticsService.getAllLike();
    }

    @GetMapping("/like/entity")
    public Integer getLikeEntity(@RequestParam(required = false, defaultValue = "0") Integer entityId)
    {
        return statisticsService.getLikeEntity(entityId);
    }

    @GetMapping("/message")
    public Integer getAllMessage()
    {
        return statisticsService.getAllMessage();
    }

    @GetMapping("/message/all")
    public ResponseEntity<?> getMessage(@RequestParam(required = false, defaultValue = "0") Integer firstUserId,
                          @RequestParam(required = false, defaultValue = "0") Integer secondUserId)
    {
        return statisticsService.getMessage(firstUserId, secondUserId);
    }

    @GetMapping("/message/dialog")
    public ResponseEntity<?> getMessageByDialog(@RequestParam(required = false, defaultValue = "0") Integer dialogId)
    {
        return statisticsService.getMessageByDialog(dialogId);
    }

    @GetMapping("/post")
    public Integer getAllPost()
    {
        return statisticsService.getAllPost();
    }

    @GetMapping("/post/user")
    public ResponseEntity<?> getAllPostByUser(@RequestParam(required = false, defaultValue = "0") Integer userId)
    {
        return statisticsService.getAllPostByUser(userId);
    }

    @GetMapping("/tag")
    public Integer getAllTags()
    {
        return statisticsService.getAllTags();
    }

    @GetMapping("/tag/post")
    public ResponseEntity<?> getTagsByPost(@RequestParam(required = false, defaultValue = "0") Integer postId)
    {
        return statisticsService.getTagsByPost(postId);
    }

    @GetMapping("/user")
    public Integer getAllUsers()
    {
        return statisticsService.getAllUsers();
    }

    @GetMapping("/user/city")
    public ResponseEntity<?> getAllUsersByCity(@RequestParam(required = false, defaultValue = "") String city)
    {
        return statisticsService.getAllUsersByCity(city);
    }

    @GetMapping("/user/country")
    public ResponseEntity<?> getAllUsersByCountry(@RequestParam(required = false, defaultValue = "") String country)
    {
        return statisticsService.getAllUsersByCountry(country);
    }
}
