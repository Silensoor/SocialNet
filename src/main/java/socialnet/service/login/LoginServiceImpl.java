package socialnet.service.login;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import socialnet.dto.*;
import socialnet.dto.LoginRs;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final JdbcTemplate jdbcTemplate;

    private Persons persons;
    private String jwt;

    public Object getLogin(LoginRq loginRq) {

        if (checkLoginAndPassword(loginRq.getEmail(), loginRq.getPassword()) == true) {
            jwt = getToken(loginRq);
            System.out.println("ok");
            return setLoginRs(jwt); //заполнить поля
        } else {
            System.out.println("no ok");
            return setLoginErrorRs(); //заполнить поля
        }
    }

    public Object getMe(String authorization) {

        return setLoginRs(jwt);
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

    public String getToken(LoginRq loginRq) {

        long timeToLive = 300000;
        byte[] secret = new byte[300];

        return Jwts.builder()
                .setSubject(loginRq.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeToLive))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public ErrorRs setLoginErrorRs() {

        ErrorRs loginErrorRs = new ErrorRs();

        loginErrorRs.setError(""); //??????????????
        loginErrorRs.setErrorDescription(""); //????????????
        loginErrorRs.setErrorDescription(""); //????????????

        return loginErrorRs;
    }

    public WeatherRs setLoginWeather(WeatherRs weatherRs) {

        weatherRs.setCity(persons.getCity());
        weatherRs.setTemp(""); //?
        weatherRs.setDate(""); //?
        weatherRs.setClouds(""); //?

        return weatherRs;
    }

    public CurrencyRs setCurrencyRs(CurrencyRs currencyRs) {

        currencyRs.setEuro(""); //?
        currencyRs.setUsd(""); //?

        return currencyRs;
    }

    public PersonRs setLoginRs(PersonRs personRs, CurrencyRs currencyRs, WeatherRs weatherRs, String jwt) {

        weatherRs = setLoginWeather(weatherRs);
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

    public LoginRs setLoginRs(String jwt) {
        WeatherRs loginWeather = new WeatherRs();
        CurrencyRs currencyRs = new CurrencyRs();
        PersonRs loginData = new PersonRs();

        loginData = setLoginRs(loginData, currencyRs, loginWeather, jwt);

        LoginRs loginRs = new LoginRs();
        loginRs.setData(loginData);
        loginRs.setItemPerPage(0); //?
        loginRs.setOffset(0); //?
        loginRs.setPerPage(0); //?
        loginRs.setTimestamp(1670773804); //?
        loginRs.setTotal(0); //?

        return loginRs;
    }

    public boolean checkLoginAndPassword(String email, String password) {

        for (Persons personInDb : personList()) {
            if ((personInDb.getEmail().equals(email) && personInDb.getPassword().equals(password))) {
                persons = personInDb;
                return true;
            }
        }

        return false;
    }

    public List<Persons> personList() {
        return jdbcTemplate.query("SELECT * FROM public.persons", new BeanPropertyRowMapper<>(Persons.class));
    }
}
