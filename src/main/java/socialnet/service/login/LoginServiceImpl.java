package socialnet.service.login;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import socialnet.dto.*;
import socialnet.dto.login.LoginCurrency;
import socialnet.dto.login.LoginRs;
import socialnet.security.jwt.JwtUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final JdbcTemplate jdbcTemplate;

    private Persons persons;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public Object getLogin(LoginRq loginRq) {

        if (checkLoginAndPassword(loginRq.getEmail(), loginRq.getPassword()) == true) {
            return setLoginRs(getToken(loginRq)); //заполнить поля
        } else {
            return setLoginErrorRs(); //заполнить поля
        }
    }

    public Object getMe(String getToken) {

        return CommonRsComplexRs();
    }

    public String getToken (LoginRq loginRq) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRq.getEmail(), loginRq.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return jwt;
    }
    public CommonRsComplexRs CommonRsComplexRs() {

        CommonRsComplexRs meRs = new CommonRsComplexRs();

        meRs.setData(null);
        meRs.setOffset(null);
        meRs.setTimestamp(null);
        meRs.setTotal(null);
        meRs.setItemPerPage(null);
        meRs.setPerPage(null);

        return meRs;
    }

    public ErrorRs setLoginErrorRs() {

        ErrorRs loginErrorRs = new ErrorRs();

        loginErrorRs.setError(null);
        loginErrorRs.setErrorDescription(null);
        loginErrorRs.setErrorDescription(null);

        return loginErrorRs;
    }

    public WeatherRs setLoginWeather(WeatherRs weatherRs) {

        weatherRs.setCity(persons.getCity());
        weatherRs.setTemp(null); //?
        weatherRs.setDate(null); //?
        weatherRs.setClouds(null); //?

        return weatherRs;
    }

    public LoginCurrency setLoginCurrency(LoginCurrency loginCurrency) {

        loginCurrency.setEuro(null); //?
        loginCurrency.setUsd(null); //?

        return loginCurrency;
    }

    public PersonRs setLoginData(PersonRs personRs, LoginCurrency loginCurrency, WeatherRs weatherRs, String jwt) {

        weatherRs = setLoginWeather(weatherRs);
        loginCurrency = setLoginCurrency(loginCurrency);
        personRs.setAbout(null); //?
        personRs.setCity(persons.getCity());
        personRs.setCountry(persons.getCountry());
        personRs.setBirthDate(persons.getBirth_date());
        personRs.setCurrency(loginCurrency);
        personRs.setWeather(weatherRs);
        personRs.setEmail(persons.getEmail());
        personRs.setFirstName(persons.getFirst_name());
        personRs.setFriendStatus(null); //?
        personRs.setId(persons.getId());
        personRs.setIsBlocked(null);
        personRs.setIsBlockedByCurrentUser(false); //?
        personRs.setLastName(persons.getLast_name());
        personRs.setLastOnlineTime(persons.getLast_online_time());
        personRs.setMessagesPermission(persons.getMessage_permissions());
        personRs.setOnline(persons.getOnline_status());
        personRs.setPhone(persons.getPhone());
        personRs.setPhoto(persons.getPhoto());
        personRs.setRegDate(persons.getReg_date());
        personRs.setToken(jwt);
        personRs.setUserDeleted(persons.getIs_deleted());

        return personRs;
    }

    public LoginRs setLoginRs(String jwt) {
        WeatherRs loginWeather = new WeatherRs();
        LoginCurrency loginCurrency = new LoginCurrency();
        PersonRs loginData = new PersonRs();

        loginData = setLoginData(loginData, loginCurrency, loginWeather, jwt);

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
