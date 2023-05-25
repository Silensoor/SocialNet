package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.SortedMap;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.RegionStatisticsRs;
import socialnet.service.StatisticsService;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "statistics-controller", description = "Get statistics by social network")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/city")
    @ApiOperation(value = "get the number of all cities")
    public Integer getAllCities()
    {
        return statisticsService.getAllCities();
    }

    @GetMapping("/city/all")
    @ApiOperation(value = "get cities with number of users")
    public RegionStatisticsRs[] getCitiesUsers()
    {
        return statisticsService.getCitiesUsers();
    }

    @GetMapping("/comment/post")
    @ApiOperation(value = "get the number of comments by post id")
    public Integer getCommentsByPost(@RequestParam(required = false, defaultValue = "0") Integer postId)
    {
       return statisticsService.getCommentsByPost(postId);
    }

    @GetMapping("/country")
    @ApiOperation(value = "get the number of all countries")
    public Integer getCountry()
    {
        return statisticsService.getCountry();
    }

    @GetMapping("/country/all")
    @ApiOperation(value = "get countries with number of all users")
    public RegionStatisticsRs[] getCountryUsers()
    {
        return statisticsService.getCountryUsers();
    }

    @GetMapping("/dialog")
    @ApiOperation(value = "get the number of all dialogs")
    public Integer getDialog()
    {
        return statisticsService.getDialog();
    }

    @GetMapping("/dialog/user")
    @ApiOperation(value = "get the number of dialogs by user id")
    public Integer getDialogsUser(@RequestParam(required = false, defaultValue = "0") Integer userId)
    {
        return statisticsService.getDialogsUser(userId);
    }

    @GetMapping("/like")
    @ApiOperation(value = "get the number of all likes")
    public Integer getAllLike()
    {
        return statisticsService.getAllLike();
    }

    @GetMapping("/like/entity")
    @ApiOperation(value = "get the number of likes by post or comment id")
    public Integer getLikeEntity(@RequestParam(required = false, defaultValue = "0") Integer entityId)
    {
        return statisticsService.getLikeEntity(entityId);
    }

    @GetMapping("/message")
    @ApiOperation(value = "get the number of all messages")
    public Integer getAllMessage()
    {
        return statisticsService.getAllMessage();
    }

    @GetMapping("/message/all")
    @ApiOperation(value = "get the number of messages by id's of two persons. This method return map where key is" +
            " description who author, and who recipient. And value is number of message")
    public SortedMap<String, Integer> getMessage(@RequestParam(required = false, defaultValue = "0") Integer firstUserId,
                                               @RequestParam(required = false, defaultValue = "0") Integer secondUserId)
    {
        return statisticsService.getMessage(firstUserId, secondUserId);
    }

    @GetMapping("/message/dialog")
    @ApiOperation(value = "get the number of all messages by dialog id")
    public Integer getMessageByDialog(@RequestParam(required = false, defaultValue = "0") Integer dialogId)
    {
        return statisticsService.getMessageByDialog(dialogId);
    }

    @GetMapping("/post")
    @ApiOperation(value = "get the number of all posts")
    public Integer getAllPost()
    {
        return statisticsService.getAllPost();
    }

    @GetMapping("/post/user")
    @ApiOperation(value = "get the number of post by user id")
    public Integer getAllPostByUser(@RequestParam(required = false, defaultValue = "0") Integer userId)
    {
        return statisticsService.getAllPostByUser(userId);
    }

    @GetMapping("/tag")
    @ApiOperation(value = "get the number of all tags")
    public Integer getAllTags()
    {
        return statisticsService.getAllTags();
    }

    @GetMapping("/tag/post")
    @ApiOperation(value = "get the number of tags by post id")
    public Integer getTagsByPost(@RequestParam(required = false, defaultValue = "0") Integer postId)
    {
        return statisticsService.getTagsByPost(postId);
    }

    @GetMapping("/user")
    @ApiOperation(value = "get the number of all users")
    public Integer getAllUsers()
    {
        return statisticsService.getAllUsers();
    }

    @GetMapping("/user/city")
    @ApiOperation(value = "get the number of all users by city name")
    public Integer getAllUsersByCity(@RequestParam(required = false, defaultValue = "") String city)
    {
        return statisticsService.getAllUsersByCity(city);
    }

    @GetMapping("/user/country")
    @ApiOperation(value = "get the number of all users by country name")
    public Integer getAllUsersByCountry(@RequestParam(required = false, defaultValue = "") String country)
    {
        return statisticsService.getAllUsersByCountry(country);
    }
}
