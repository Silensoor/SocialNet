package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
//import socialnet.security.jwt.JwtUtils;

@Service
@RequiredArgsConstructor
public class RecoverService {
   // private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    public ResponseEntity<?> recoverUser(String authorization) {

 /*       if (!jwtUtils.validateJwtToken(authorization)) {//401
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userName = jwtUtils.getUserNameFromJwtToken(authorization);
        if (userName.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs("EmptyEmailException","Field 'email' is empty",
                            System.currentTimeMillis()), HttpStatus.BAD_REQUEST);  //400
        }
*/
        String userName = "mets@inbox.ru";
        Person person = personRepository.findByEmail(userName);

        if (person == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  //403
        }

        return new ResponseEntity<>(new CommonRs(new ComplexRs("OK")), HttpStatus.OK);
    }
}
