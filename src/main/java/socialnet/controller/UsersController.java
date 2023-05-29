package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.UserRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.PersonRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.model.SearchOptions;
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
    @Operation(summary = "get information about me")
    public CommonRs<PersonRs> getMyProfile(@RequestHeader(name = "authorization") String authorization) {
        return personService.getMyProfile(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/{id}")
    @Operation(summary = "get user by id")
    public CommonRs<PersonRs> getUserById(@RequestHeader(name = "authorization") String authorization,
                                          @PathVariable(name = "id") Integer id) {
        return personService.getUserById(authorization, id);
    }

    @OnlineStatusUpdatable
    @GetMapping("/search")
    @Operation(summary = "search post by query")
    public CommonRs<List<PersonRs>> findPersons(@RequestHeader String authorization,
                                                @RequestParam(name = "age_from", required = false, defaultValue = "0")
                                                Integer ageFrom,
                                                @RequestParam(name = "age_to", required = false, defaultValue = "0")
                                                Integer ageTo,
                                                @RequestParam(required = false, defaultValue = "")
                                                String city,
                                                @RequestParam(required = false, defaultValue = "")
                                                String country,
                                                @RequestParam(name = "first_name", required = false, defaultValue = "")
                                                String firstName,
                                                @RequestParam(name = "last_name", required = false, defaultValue = "")
                                                String lastName,
                                                @RequestParam(required = false, defaultValue = "0")
                                                Integer offset,
                                                @RequestParam(required = false, defaultValue = "20")
                                                Integer perPage) {


        return findService.findPersons(SearchOptions.builder()
                .jwtToken(authorization)
                .ageFrom(ageFrom)
                .ageTo(ageTo)
                .city(city)
                .country(country)
                .firstName(firstName)
                .lastName(lastName)
                .offset(offset)
                .perPage(perPage)
                .build());
    }

    @OnlineStatusUpdatable
    @PutMapping("/me")
    @Operation(summary = "update information about me")
    public ResponseEntity<CommonRs<PersonRs>> updateUserInfo(@RequestHeader("authorization") String authorization,
                                            @RequestBody UserRq userData) {
        return personService.updateUserInfo(authorization, userData);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/me")
    @Operation(summary = "delete information about me")
    public CommonRs<ComplexRs> deleteUser(@RequestHeader("authorization") String authorization) {
        return personService.delete(authorization);
    }

    @OnlineStatusUpdatable
    @PostMapping("/me/recover")
    @Operation(summary = "recover information about me")
    public CommonRs<ComplexRs> recoverUser(@RequestHeader("authorization") String authorization) {
        return personService.recover(authorization);
    }

}