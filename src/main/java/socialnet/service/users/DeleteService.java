package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.ErrorRs;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

@Service
@RequiredArgsConstructor
public class DeleteService {
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;

    public ResponseEntity<?> deleteUser(String authorization) {


        if (!jwtUtils.validateJwtToken(authorization)) {//401
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userName = jwtUtils.getUserEmail(authorization);
        if (userName.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs("Empty Email Exception","Field 'email' is empty"), HttpStatus.BAD_REQUEST);  //400
        }

        Person person = personRepository.findByEmail(userName);

        if (person.getIsDeleted() || (person == null)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  //403
        }

        personRepository.deleteUser(userName);

        ComplexRs data = new ComplexRs("OK");
        return new ResponseEntity<>(new CommonRs<>(data), HttpStatus.OK);
    }
}
