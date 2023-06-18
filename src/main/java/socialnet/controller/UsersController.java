package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.UserRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.service.FindService;
import socialnet.service.PersonService;


import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final PersonService personService;
    private final FindService findService;

    @GetMapping("/me")
    public CommonRs<PersonRs> getMyProfile(@RequestHeader(name = "authorization") String authorization) {
        return personService.getMyProfile(authorization);
    }

    @GetMapping("/{id}")
    public Object Id(@RequestHeader(name = "authorization") String authorization, @PathVariable(name = "id") Integer id) {
        return personService.getUserById(authorization, id);
    }

    @GetMapping("/search")
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

        return findService.findPersons(authorization, age_from, age_to, city, country, first_name, last_name,
                offset, perPage);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserInfo(@RequestHeader("authorization") String authorization,
                                            @RequestBody UserRq userData) {
        return personService.updateUserInfo(authorization, userData);
    }

}