package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.UserRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.FindService;
import socialnet.service.PersonService;


import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "users-controller", description = "Get user. Get, update, delete, recover personal info. User search")
public class UsersController {

    private final PersonService personService;
    private final FindService findService;

    @OnlineStatusUpdatable
    @GetMapping("/me")
    @ApiOperation(value = "get information about me")
    public CommonRs<PersonRs> getMyProfile(@RequestHeader(name = "authorization") String authorization) {
        return personService.getMyProfile(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/{id}")
    @ApiOperation(value = "get user by id")
    public CommonRs<PersonRs> getUserById(@RequestHeader(name = "authorization") String authorization,
                                          @PathVariable(name = "id") Integer id) {
        return personService.getUserById(authorization, id);
    }

    @OnlineStatusUpdatable
    @GetMapping("/search")
    @ApiOperation(value = "search post by query")
    public CommonRs<List<PersonRs>> findPersons(@RequestHeader String authorization,
                                                @RequestParam(required = false, defaultValue = "0")
                                                Integer age_from,
                                                @RequestParam(required = false, defaultValue = "0")
                                                Integer age_to,
                                                @RequestParam(required = false, defaultValue = "")
                                                String city,
                                                @RequestParam(required = false, defaultValue = "")
                                                String country,
                                                @RequestParam(required = false, defaultValue = "")
                                                String first_name,
                                                @RequestParam(required = false, defaultValue = "")
                                                String last_name,
                                                @RequestParam(required = false, defaultValue = "0")
                                                Integer offset,
                                                @RequestParam(required = false, defaultValue = "20")
                                                Integer perPage) {


        return findService.findPersons(authorization, age_from, age_to, city, country, first_name,
                last_name, offset, perPage);

        return findService.findPersons(authorization, age_from, age_to, city, country, first_name, last_name,
                offset, perPage);

    }

    @OnlineStatusUpdatable
    @PutMapping("/me")
    @ApiOperation(value = "update information about me")
    public ResponseEntity<?> updateUserInfo(@RequestHeader("authorization") String authorization,
                                            @RequestBody UserRq userData) {
        return personService.updateUserInfo(authorization, userData);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/me")
    @ApiOperation(value = "delete information about me")
    public CommonRs deleteUser(@RequestHeader("authorization") String authorization) {
        return personService.delete(authorization);
    }

    @OnlineStatusUpdatable
    @PostMapping("/me/recover")
    @ApiOperation(value = "recover information about me")
    public CommonRs recoverUser(@RequestHeader("authorization") String authorization) {
        return personService.recover(authorization);
    }

}