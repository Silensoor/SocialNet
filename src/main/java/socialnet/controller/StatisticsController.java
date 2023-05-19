package socialnet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.RegionStatisticsRs;
import socialnet.service.StatisticsService;

import java.util.TreeMap;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "statistics-controller", description = "Get statistics by social network")
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
    public Integer getCommentsByPost(@RequestParam(required = false, defaultValue = "0") Integer postId)
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
    public Integer getDialogsUser(@RequestParam(required = false, defaultValue = "0") Integer userId)
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
    public TreeMap<String, Integer> getMessage(@RequestParam(required = false, defaultValue = "0") Integer firstUserId,
                                               @RequestParam(required = false, defaultValue = "0") Integer secondUserId)
    {
        return statisticsService.getMessage(firstUserId, secondUserId);
    }

    @GetMapping("/message/dialog")
    public Integer getMessageByDialog(@RequestParam(required = false, defaultValue = "0") Integer dialogId)
    {
        return statisticsService.getMessageByDialog(dialogId);
    }

    @GetMapping("/post")
    public Integer getAllPost()
    {
        return statisticsService.getAllPost();
    }

    @GetMapping("/post/user")
    public Integer getAllPostByUser(@RequestParam(required = false, defaultValue = "0") Integer userId)
    {
        return statisticsService.getAllPostByUser(userId);
    }

    @GetMapping("/tag")
    public Integer getAllTags()
    {
        return statisticsService.getAllTags();
    }

    @GetMapping("/tag/post")
    public Integer getTagsByPost(@RequestParam(required = false, defaultValue = "0") Integer postId)
    {
        return statisticsService.getTagsByPost(postId);
    }

    @GetMapping("/user")
    public Integer getAllUsers()
    {
        return statisticsService.getAllUsers();
    }

    @GetMapping("/user/city")
    public Integer getAllUsersByCity(@RequestParam(required = false, defaultValue = "") String city)
    {
        return statisticsService.getAllUsersByCity(city);
    }

    @GetMapping("/user/country")
    public Integer getAllUsersByCountry(@RequestParam(required = false, defaultValue = "") String country)
    {
        return statisticsService.getAllUsersByCountry(country);
    }
}
