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
public class GetUserInfoService {
    private final PersonMapper personMapper;
    private final PersonRepository personRepository;
    private final JwtUtils jwtUtils;

    public ResponseEntity<?> getUserInfo(String authorization) {

        if (!jwtUtils.validateJwtToken(authorization)) {//401
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userName = jwtUtils.getUserEmail(authorization);
        if (userName.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs("EmptyEmailException","Field 'email' is empty"), HttpStatus.BAD_REQUEST);  //400
        }

        Person person = personRepository.findByEmail(userName);
        if (person.getIsDeleted()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  //403
        }

        PersonRs personRs = personMapper.toDTO(person);

        return ResponseEntity.ok(new CommonRs(personRs));
    }
}
