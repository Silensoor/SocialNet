package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.response.*;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.PersonMapper;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.service.PostService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FindUserService {
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    private final PostService postService;

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

    public CommonRs<List<PersonRs>> findPersonsUsingGET(Object[] args) {
        String email = jwtUtils.getUserEmail((String) args[0]);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        List<Person> personList = new ArrayList<>();
        List<PersonRs> personRsList = new ArrayList<>();
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            String sql = createSqlPerson(args);
            if (!sql.equals("SELECT * FROM persons WHERE is_deleted=false AND is_blocked=false AND ")) {
                personList = personRepository.findPersonsQuery(sql);
                if (personList == null) {
                    personList = new ArrayList<>();
                }
            }
            for (Person person : personList) {
                PersonRs personRs = personMapper.toDTO(person);
                personRsList.add(personRs);
            }
        }
        personRsList.sort(Comparator.comparing(PersonRs::getRegDate));
        return new CommonRs<>(personRsList, (Integer) args[8], (Integer) args[7], (Integer) args[8],
                System.currentTimeMillis(), (long) personRsList.size());
    }


    private String createSqlPerson(Object[] args) {
        String sql = "SELECT * FROM persons WHERE is_deleted=false AND is_blocked=false AND ";
        if (args[1] != null && (Integer) args[1] > 0) {
            val ageFrom = searchDate((Integer) args[1]);
            sql = sql + " birth_date < '" + ageFrom + "' AND ";
        }
        if (args[2] != null && (Integer) args[2] > 0) {
            val ageTo = searchDate((Integer) args[2]);
            sql = sql + " birth_date > '" + ageTo + "' AND ";
        }
        if (args[3] != null && args[3] != "") {
            sql = sql + " city = '" + args[3] + "' AND ";
        }
        if (args[4] != null && args[4] != "") {
            sql = sql + " country = '" + args[4] + "' AND ";
        }
        if (args[5] != null && args[5] != "") {
            sql = sql + " first_name = '" + args[5] + "' AND ";
        }
        if (args[6] != null && args[6] != "") {
            sql = sql + " last_name = '" + args[6] + "' AND ";
        }
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")) {
            return sql.substring(0, sql.length() - 5);
        }
        return sql;
    }

    private Timestamp searchDate(Integer age){
        val timestamp = new Timestamp(new Date().getTime());
        timestamp.setYear(timestamp.getYear() - age);
        return timestamp;
    }
}
