package socialnet.service.login;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import socialnet.dto.ErrorRs;
import socialnet.dto.LoginRq;
import socialnet.dto.PersonRs;
import socialnet.dto.WeatherRs;
import socialnet.dto.login.*;

import javax.naming.AuthenticationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private Persons person;

    private final JdbcTemplate jdbcTemplate;

    public Object getLogin(LoginRq loginRq) throws AuthenticationException {

        if (checkLoginAndPassword(loginRq.getEmail(), loginRq.getPassword()) == true) {
            return setLoginRs(); //заполнить поля
        } else {
            return setLoginErrorRs(); //заполнить поля
        }
    }

    public ErrorRs setLoginErrorRs() {

        ErrorRs loginErrorRs = new ErrorRs();

        loginErrorRs.setError(null);
        loginErrorRs.setErrorDescription(null);
        loginErrorRs.setErrorDescription(null);

        return loginErrorRs;
    }

    public WeatherRs setLoginWeather(WeatherRs weatherRs) {

        weatherRs.setCity(person.getCity());
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

    public PersonRs setLoginData(PersonRs personRs, LoginCurrency loginCurrency, WeatherRs weatherRs) {

        weatherRs = setLoginWeather(weatherRs);
        loginCurrency = setLoginCurrency(loginCurrency);
        personRs.setAbout(null); //?
        personRs.setCity(person.getCity());
        personRs.setCountry(person.getCountry());
        personRs.setBirthDate(person.getBirth_date());
        personRs.setCurrency(loginCurrency);
        personRs.setWeather(weatherRs);
        personRs.setEmail(person.getEmail());
        personRs.setFirstName(person.getFirst_name());
        personRs.setFriendStatus(null); //?
        personRs.setId(person.getId());
        personRs.setIsBlocked(person.getIs_blocked());
        personRs.setIsBlockedByCurrentUser(false); //?
        personRs.setLastName(person.getLast_name());
        personRs.setLastOnlineTime(person.getLast_online_time());
        personRs.setMessagesPermission(person.getMessage_permissions());
        personRs.setOnline(person.getOnline_status());
        personRs.setPhone(person.getPhone());
        personRs.setPhoto(person.getPhoto());
        personRs.setRegDate(person.getReg_date());
        personRs.setToken(null);
        personRs.setUserDeleted(person.getIs_deleted());

        return personRs;
    }

    public LoginRs setLoginRs() {
        WeatherRs loginWeather = new WeatherRs();
        LoginCurrency loginCurrency = new LoginCurrency();
        PersonRs loginData = new PersonRs();

        loginData = setLoginData(loginData, loginCurrency, loginWeather);

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
                person = personInDb;
                return true;
            }
        }
        return false;
    }

    public List<Persons> personList() {
        return jdbcTemplate.query("SELECT * FROM public.persons", new BeanPropertyRowMapper<>(Persons.class));
    }
}
