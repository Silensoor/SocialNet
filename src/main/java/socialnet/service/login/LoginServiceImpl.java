package socialnet.service.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import socialnet.dto.*;
import socialnet.dto.LoginRs;
import socialnet.model.Persons;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtils jwtUtils;
    private String jwt;
    private final AuthenticationManager authenticationManager;


    public Object getLogin(LoginRq loginRq) {



        Persons persons;
        if ((persons = checkLoginAndPassword(loginRq.getEmail(), loginRq.getPassword())) != null) {
            jwt = jwtUtils.generateJwtToken(loginRq.getEmail());
            authenticated(loginRq);
            return setLoginRs(jwt, persons); //заполнить поля
        } else {
            ErrorRs errorRs = new ErrorRs();
            errorRs.setError("400");
            errorRs.setErrorDescription("Field 'email' is empty");
            errorRs.setTimestamp(String.valueOf(new Timestamp(System.currentTimeMillis())));

            return errorRs;
        }
    }

    public Object getMe(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        Persons persons = jdbcTemplate.query("SELECT * FROM persons where email=?", new Object[]{email},
                new BeanPropertyRowMapper<>(Persons.class)).stream().findAny().orElse(null);
        return setLoginRs(jwt, persons);
    }

    public Object getLogout(String authorization) {

        return setCommonRsComplexRs(setComplexRs());
    }

    public CommonRsComplexRs setCommonRsComplexRs(ComplexRs setComplexRs) {
        CommonRsComplexRs commonRsComplexRs = new CommonRsComplexRs();
        commonRsComplexRs.setData(setComplexRs);
        commonRsComplexRs.setOffset(0);
        commonRsComplexRs.setTimestamp(0);
        commonRsComplexRs.setTotal(0);
        commonRsComplexRs.setItemPerPage(0);
        commonRsComplexRs.setPerPage(0);
        return commonRsComplexRs;
    }

    public ComplexRs setComplexRs() {
        ComplexRs complexRs = new ComplexRs();

        complexRs.setId(0);
        complexRs.setCount(0);
        complexRs.setMessage("");
        complexRs.setMessageId(0);

        return complexRs;
    }

    public WeatherRs setLoginWeather(WeatherRs weatherRs, Persons persons) {

        weatherRs.setCity(persons.getCity());
        weatherRs.setTemp(""); //?
        weatherRs.setDate(new Date()); //?
        weatherRs.setClouds(""); //?

        return weatherRs;
    }

    public CurrencyRs setCurrencyRs(CurrencyRs currencyRs) {

        currencyRs.setEuro(""); //?
        currencyRs.setUsd(""); //?

        return currencyRs;
    }

    public PersonRs setPersonRs(CurrencyRs currencyRs, WeatherRs weatherRs, String jwt, Persons persons) {
        PersonRs personRs = new PersonRs();
        weatherRs = setLoginWeather(weatherRs, persons);
        currencyRs = setCurrencyRs(currencyRs);
        personRs.setAbout(persons.getAbout());
        personRs.setCity(persons.getCity());
        personRs.setCountry(persons.getCountry());
        personRs.setBirthDate(persons.getBirth_date());
        personRs.setCurrency(currencyRs);
        personRs.setWeather(weatherRs);
        personRs.setEmail(persons.getEmail());
        personRs.setFirstName(persons.getFirst_name());
        personRs.setFriendStatus(""); //???????????????????????
        personRs.setId(persons.getId());
        personRs.setIsBlocked(persons.getIs_blocked());
        personRs.setIsBlockedByCurrentUser(false); //????????????????????
        personRs.setLastName(persons.getLast_name());
        personRs.setLastOnlineTime(persons.getLast_online_time());
        personRs.setMessagesPermission(persons.getMessage_permissions());
        personRs.setOnline(true); //???????????????
        personRs.setPhone(persons.getPhone());
        personRs.setPhoto(persons.getPhoto());
        personRs.setRegDate(persons.getReg_date());
        personRs.setToken(jwt);
        personRs.setUserDeleted(persons.getIs_deleted());
        return personRs;
    }

    public LoginRs setLoginRs(String jwt, Persons persons) {
        WeatherRs loginWeather = new WeatherRs();
        CurrencyRs currencyRs = new CurrencyRs();
        PersonRs personRs1 = setPersonRs(currencyRs, loginWeather, jwt, persons);
        LoginRs loginRs = new LoginRs();
        loginRs.setData(personRs1);
        loginRs.setItemPerPage(0); //?
        loginRs.setOffset(0); //?
        loginRs.setPerPage(0); //?
        loginRs.setTimestamp(System.currentTimeMillis()); //?
        loginRs.setTotal(0); //?
        return loginRs;
    }

    public Persons checkLoginAndPassword(String email, String password) {

        Persons persons = jdbcTemplate.query("SELECT * FROM persons where email=?", new Object[]{email},
                new BeanPropertyRowMapper<>(Persons.class)).stream().findAny().orElse(null);
        if (persons != null && new BCryptPasswordEncoder().matches(password, persons.getPassword())) {
            log.info(persons.getFirst_name() + " авторизован");
            return persons;
        }
        return null;
    }
    private void authenticated(LoginRq loginRq){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRq.getEmail(), loginRq.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
