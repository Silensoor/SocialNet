package socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import socialnet.api.request.LoginRq;
import socialnet.api.response.*;
import socialnet.exception.EmptyEmailException;
import socialnet.model.Person;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class PersonService {
    private final JwtUtils jwtUtils;
    private String jwt;
    private final AuthenticationManager authenticationManager;
    private final PersonRepository personRepository;
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    public Object getLogin(LoginRq loginRq) {

        Person person;
        if ((person = checkLoginAndPassword(loginRq.getEmail(), loginRq.getPassword())) != null) {
            jwt = jwtUtils.generateJwtToken(loginRq.getEmail());
            authenticated(loginRq);

            return setLoginRs(jwt, person);
        } else {
            throw new EmptyEmailException("invalid username or password");
        }
    }

    public Object getMe(String authorization) {

        String email = jwtUtils.getUserEmail(authorization);
        Person person = personRepository.findByEmail(email);
        return setLoginRs(jwt, person);
    }

    public Object getLogout(String authorization) {

        return setCommonRs(setComplexRs());
    }

    public Object getUserById(String authorization, Integer id) {
        Person person = findUser(id);
        log.info("вход на страницу пользователя " + person.getFirstName());
        return setLoginRs(authorization, person);
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

    public WeatherRs setLoginWeather(WeatherRs weatherRs, Person person) {

        weatherRs.setCity(person.getCity());
        weatherRs.setTemp("");
        weatherRs.setDate(DATE_FORMATTER.format(new Date()));
        weatherRs.setClouds("");

        return weatherRs;
    }

    public CurrencyRs setCurrencyRs(CurrencyRs currencyRs) {

        currencyRs.setEuro("");
        currencyRs.setUsd("");

        return currencyRs;
    }

    public PersonRs setPersonRs(CurrencyRs currencyRs, WeatherRs weatherRs, String jwt, Person person) {
        PersonRs personRs = new PersonRs();
        weatherRs = setLoginWeather(weatherRs, person);
        currencyRs = setCurrencyRs(currencyRs);
        personRs.setAbout(person.getAbout());
        personRs.setCity(person.getCity());
        personRs.setCountry(person.getCountry());
        personRs.setBirthDate(person.getBirthDate());
        personRs.setCurrency(currencyRs);
        personRs.setWeather(weatherRs);
        personRs.setEmail(person.getEmail());
        personRs.setFirstName(person.getFirstName());
        personRs.setFriendStatus("");
        personRs.setId(person.getId());
        personRs.setIsBlocked(person.getIsBlocked());
        personRs.setIsBlockedByCurrentUser(false);
        personRs.setLastName(person.getLastName());
        personRs.setLastOnlineTime(person.getLastOnlineTime());
        personRs.setMessagesPermission(person.getMessagePermissions());
        personRs.setOnline(true);
        personRs.setPhone(person.getPhone());
        personRs.setPhoto(person.getPhoto());
        personRs.setRegDate(person.getRegDate());
        personRs.setToken(jwt);
        personRs.setUserDeleted(person.getIsDeleted());
        return personRs;
    }

    public CommonRs<PersonRs> setLoginRs(String jwt, Person person) {
        WeatherRs loginWeather = new WeatherRs();
        CurrencyRs currencyRs = new CurrencyRs();
        PersonRs personRs = setPersonRs(currencyRs, loginWeather, jwt, person);
        CommonRs<PersonRs> commonRs = new CommonRs<>();
        commonRs.setData(personRs);
        commonRs.setTimestamp(System.currentTimeMillis());
        return commonRs;
    }

    public Person checkLoginAndPassword(String email, String password) {

        Person person = personRepository.findByEmail(email);

        if (person != null && new BCryptPasswordEncoder().matches(password, person.getPassword())) {
            log.info(person.getFirstName() + " авторизован");
            return person;
        }
        return null;
    }

    private void authenticated(LoginRq loginRq) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRq.getEmail(), loginRq.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
