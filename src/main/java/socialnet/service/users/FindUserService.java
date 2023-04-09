package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.PersonRs;
import socialnet.mappers.PersonMapper;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

@Service
@RequiredArgsConstructor
public class FindUserService {
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public ResponseEntity<?> findUserById(String authorization, Integer id) {

        if (!jwtUtils.validateJwtToken(authorization)) {//401
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userName = jwtUtils.getUserEmail(authorization);
        if (userName.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs("EmptyEmailException", "Field 'email' is empty"), HttpStatus.BAD_REQUEST);  //400
        }

        Person person = personRepository.findByEmail(userName);
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  //403
        }
/*

        Integer itemPerPage = 20;
        Integer offset = 0;
        Integer perPage = 20;
        Long timeStamp = System.currentTimeMillis();
        Long total = 0L;
*/

        PersonRs personRs = personMapper.toDTO(person);

        return ResponseEntity.ok(new CommonRs(personRs));
    }
}
