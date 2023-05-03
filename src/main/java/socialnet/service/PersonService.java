package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import socialnet.api.request.*;
import socialnet.api.response.*;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.PersonMapper;
import socialnet.mappers.UserDtoMapper;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.time.LocalDate;
import java.util.ResourceBundle;


@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class PersonService {
    private final JwtUtils jwtUtils;
    private String jwt;
    private final AuthenticationManager authenticationManager;
    private final PersonRepository personRepository;
    private final WeatherService weatherService;
    private final CurrencyService currencyService;
    private final PasswordEncoder passwordEncoder;
    private static final ResourceBundle textProperties = ResourceBundle.getBundle("text");

    public CommonRs<PersonRs> getLogin(LoginRq loginRq) {

        Person person = checkLoginAndPassword(loginRq.getEmail(), loginRq.getPassword());

            jwt = jwtUtils.generateJwtToken(loginRq.getEmail());
            authenticated(loginRq);
            PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
            changePersonStatus(personRs);
            return new CommonRs<>(personRs);

    }

    public CommonRs<PersonRs> getMyProfile(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        Person person = personRepository.findByEmail(email);
        PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
        changePersonStatus(personRs);
        return new CommonRs<>(personRs);
    }

    private void changePersonStatus(PersonRs personRs) {
        personRs.setToken(jwt);
        personRs.setOnline(true);
        personRs.setIsBlockedByCurrentUser(false);
        personRs.setIsBlockedByCurrentUser(false);
        personRs.setWeather(weatherService.getWeatherByCity(personRs.getCity()));
        personRs.setCurrency(currencyService.getCurrency(LocalDate.now()));
        if (personRs.getPhoto() == null) {
            personRs.setPhoto(textProperties.getString("default.photo"));
        }

    }

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

        PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);

        personRs.setWeather(weatherService.getWeatherByCity(person.getCity()));
        personRs.setCurrency(currencyService.getCurrency(LocalDate.now()));

        return ResponseEntity.ok(new CommonRs(personRs));
    }

    public CommonRs<ComplexRs> getLogout(String authorization) {

        return setCommonRs(setComplexRs());
    }

    public CommonRs<PersonRs> getUserById(String authorization, Integer id) {
        Person person = findUser(id);
        PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
        changePersonStatus(personRs);
        return new CommonRs<>(personRs);
    }

    private Person findUser(Integer id) {
        return personRepository.findById(Long.valueOf(id));
    }

    public CommonRs<ComplexRs> setCommonRs(ComplexRs complexRs) {
        CommonRs<ComplexRs> commonRs = new CommonRs<>();
        commonRs.setData(complexRs);
        commonRs.setOffset(0);
        commonRs.setTimestamp(0L);
        commonRs.setTotal(0L);
        commonRs.setItemPerPage(0);
        commonRs.setPerPage(0);
        return commonRs;
    }

    public ComplexRs setComplexRs() {

        return ComplexRs.builder()
                .id(0)
                .count(0L)
                .message("")
                .messageId(0L)
                .build();
    }

    public Person getAuthPerson() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return personRepository.getPersonByEmail(email);
    }

    public Long getAuthPersonId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return personRepository.getPersonIdByEmail(email);
    }

    public RegisterRs setNewEmail(EmailRq emailRq) {
        //String token = emailRq.getSecret();
        personRepository.setEmail(jwtUtils.getUserEmail(emailRq.getSecret()), emailRq.getEmail());
        return new RegisterRs();
    }

    public PasswordSetRq resetPassword(String authorization, PasswordSetRq passwordSetRq) {
        personRepository.setPassword(passwordEncoder.encode(passwordSetRq.getPassword()), jwtUtils.getUserEmail(authorization));
        return new PasswordSetRq();
    }


    public Person checkLoginAndPassword(String email, String password) {

        Person person = personRepository.findByEmail(email);

        if (person == null) {

            throw new EmptyEmailException("Email is not registered");
        }

        if (!new BCryptPasswordEncoder().matches(password, person.getPassword())) {

            throw new EmptyEmailException("Incorrect password");
        }

        return person;
    }

        private void authenticated(LoginRq loginRq) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRq.getEmail(), loginRq.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public ResponseEntity<?> updateUserInfo(String authorization, UserRq userRq) {

        Person person = personRepository.findByEmail(jwtUtils.getUserEmail(authorization));

        if (person.getIsBlocked()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  //403
        }

        PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
        UserUpdateDto userUpdateDto = UserDtoMapper.INSTANCE.toDto(userRq);

        userUpdateDto.setPhoto(person.getPhoto());
        if (userUpdateDto.getPhoto() == null)
            userUpdateDto.setPhoto(textProperties.getString("default.photo"));

        personRepository.updatePersonInfo(userUpdateDto, person.getEmail());

        return ResponseEntity.ok(new CommonRs(personRs));
    }


}