package socialnet.service.users;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import socialnet.api.response.*;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.PersonMapper;
import socialnet.model.Person;
import socialnet.model.Post;
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


    /*
    @RequestHeader    String authorization,0
    @RequestParam    Optional<Integer> age_from,1
    @RequestParam Optional<Integer> age_to,2
    @RequestParam Optional<String> city,3
    @RequestParam Optional<String> first_name,4
    @RequestParam Optional<String> last_name,5
    @RequestParam Optional<Integer> offset,6
    @RequestParam Optional<Integer> perPage7

     */
    public CommonRs<List<PersonRs>> findPersonsUsingGET(Object[] args) throws ParseException {
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
            if (!sql.equals("SELECT * FROM persons WHERE")) {
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
        return new CommonRs<>(personRsList, (Integer) args[7], (Integer) args[6], (Integer) args[7],
                System.currentTimeMillis(), (long) personRsList.size());
    }


    private String createSqlPerson(Object[] args) throws ParseException {
        String sql = "SELECT * FROM persons WHERE";
        if (args[1] != null && (Integer) args[1] > 0) {
            val ageFrom = parseDate((String) args[1]);
            sql = sql + " birth_date < '" + ageFrom + "' AND ";
        }
        if (args[2] != null && (Integer) args[2] > 0) {
            val ageTo = parseDate((String) args[1]);
            sql = sql + " birth_date > '" + ageTo + "' AND ";
        }
        if (args[3] != null && args[3] != "") {
            sql = sql + " city = '" + args[3] + "' AND ";
        }
        if (args[4] != null && args[4] != "") {
            sql = sql + " first_name = '" + args[4] + "' AND ";
        }
        if (args[5] != null && args[5] != "") {
            sql = sql + " last_name = '" + args[5] + "' AND ";
        }
        String str = sql.substring(sql.length() - 5);
        if (str.equals(" AND ")) {
            return sql.substring(0, sql.length() - 5);
        }
        return sql;
    }

    private Timestamp parseDate(String str) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date date = parser.parse(str);
        return new Timestamp(date.getTime());
    }
}
