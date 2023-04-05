package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.dto.users.UserUpdateDto;
import socialnet.api.request.UserRq;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.PersonRs;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.UserDtoMapper;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.Converter;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final UserDtoMapper userDtoMapper;
    private final JwtUtils jwtUtils;
    private final Converter converter;
    public ResponseEntity<?> updateUserInfo(String authorization, UserRq userRq) {

        if (!jwtUtils.validateJwtToken(authorization)) {//401
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String userName = jwtUtils.getUserEmail(authorization);
        if (userName.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs("EmptyEmailException","Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);  //400
        }

        Person person = personRepository.findByEmail(userName);
        if (person.getIsBlocked()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  //403
        }

        PersonRs personRs = personMapper.toDTO(person);

        UserUpdateDto userUpdateDto = userDtoMapper.toDto(userRq);
        setPersonNames(userUpdateDto,person);

        personRepository.updatePersonInfo(userUpdateDto, person.getEmail());

        return ResponseEntity.ok(new CommonRs(personRs));
    }

    private void setPersonNames(UserUpdateDto userUpdateDto, Person person) {
        if (userUpdateDto.getFirstName() == null) {
            userUpdateDto.setFirstName(person.getFirstName());
        }

        if (userUpdateDto.getLastName() == null) {
            userUpdateDto.setLastName(person.getLastName());
        }
    }
}
