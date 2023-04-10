package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.CommonRsListPersonRs;
import socialnet.api.response.PersonRs;
import socialnet.service.PersonService;
import socialnet.service.users.FindUserService;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final PersonService personService;

    private final FindUserService findUserService;

    @GetMapping("/me")
    public Object Me(@RequestHeader(name = "authorization") String authorization) {
        return personService.getMe(authorization);
    }

    @GetMapping("/{id}")
    public Object Id(@RequestHeader(name = "authorization") String authorization, @PathVariable(name = "id") Integer id) {
        return personService.getUserById(authorization, id);
    }

    @GetMapping("/search")
    @ResponseBody
    public CommonRs<List<PersonRs>> findPersonsUsingGET(@RequestHeader String authorization,
                                                        @RequestParam Optional<Integer> age_from,
                                                        @RequestParam Optional<Integer> age_to,
                                                        @RequestParam Optional<String> city,
                                                        @RequestParam Optional<String> country,
                                                        @RequestParam Optional<String> first_name,
                                                        @RequestParam Optional<String> last_name,
                                                        @RequestParam Optional<Integer> offset,
                                                        @RequestParam Optional<Integer> perPage) throws ParseException {

        return findUserService.findPersonsUsingGET(
                new Object[]{
                        authorization,
                        age_from.orElse(0),
                        age_to.orElse(0),
                        city.orElse(""),
                        country.orElse(""),
                        first_name.orElse(""),
                        last_name.orElse(""),
                        offset.orElse(0),
                        perPage.orElse(20)
                }
        );
    }
}