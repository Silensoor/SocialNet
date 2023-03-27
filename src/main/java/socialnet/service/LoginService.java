package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.Authenticator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import socialnet.dto.*;
import socialnet.security.jwt.JwtUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public LoginRs login (LoginRq loginRq){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRq.getEmail(), loginRq.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        WeatherRs weatherRs = getWeatherRs();
        CurrencyRs currencyRs = getCurrency();
        DataRs dataRs = getData(loginRq,jwt,currencyRs,weatherRs);
        return getResponse(dataRs);






    }
    private LoginRs getResponse(DataRs dataRs){
        LoginRs loginRs = new LoginRs();
        loginRs.setTotal(500);
        loginRs.setTimestamp(System.currentTimeMillis());
        loginRs.setPerPage(20);
        loginRs.setOffset(0);
        loginRs.setItemPerPage(20);
        loginRs.setData(dataRs);
        return loginRs;
    }
    private WeatherRs getWeatherRs(){
        WeatherRs weatherRs = new WeatherRs();
        weatherRs.setCity("Paris");
        weatherRs.setDate(new Date());
        weatherRs.setTemp("9");
        weatherRs.setClouds("clouds");
    return weatherRs;
    }
    private CurrencyRs getCurrency(){
        CurrencyRs currencyRs = new CurrencyRs();
        currencyRs.setEuro("65");
        currencyRs.setUsd("56");
        return currencyRs;
    }
    private DataRs getData(LoginRq loginRq,String jwt,CurrencyRs currencyRs, WeatherRs weatherRs){
        DataRs dataRs = new DataRs();
        dataRs.setAbout("Iam stupid");
        dataRs.setBirth_date(new Date());
        dataRs.setCity("Paris");
        dataRs.setCurrency(currencyRs);
        dataRs.setCountry("France");
        dataRs.setEmail(loginRq.getEmail());
        dataRs.setFirst_name("Maks");
        dataRs.setFriend_status("FRIEND");
        dataRs.setId(1L);
        dataRs.setIs_blocked(false);
        dataRs.setIs_blocked_by_current_user(false);
        dataRs.setLast_name("Petrov");
        dataRs.setLast_online_time(new Date());
        dataRs.setMessages_permission("ALL");
        dataRs.setOnline(true);
        dataRs.setPhone("+7 (982) 281-15-23");
        dataRs.setPhoto("");
        dataRs.setReg_date(new Date());
        dataRs.setToken(jwt);
        dataRs.setUser_deleted(false);
        dataRs.setWeather(weatherRs);
        return dataRs;
    }
}
