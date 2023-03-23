package socialnet.security.jwt;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;

@Slf4j
public class JWTokenRepository implements CsrfTokenRepository {
    @Getter
    @Value("${auth.secret}")
    private String secret;
    @Value("${auth.timeLive}")
    private Integer timeLive;

    @Override
    public CsrfToken generateToken(HttpServletRequest httpServletRequest) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(timeLive).atZone(ZoneId.systemDefault()).toInstant());
        String token = "";
        try {
            token = Jwts.builder()
                    .setId(id)
                    .setIssuedAt(now)
                    .setNotBefore(now)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.HS256,secret)
                    .compact();
        }catch (JwtException e){
            log.debug("Alarm create token "+e.getMessage());
        }
        return new DefaultCsrfToken("x-csrf-token","_csrf",token);

    }

    @Override
    public void saveToken(CsrfToken csrfToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (Objects.nonNull(csrfToken)) {
            if (!httpServletResponse.getHeaderNames().contains(ACCESS_CONTROL_EXPOSE_HEADERS))
                httpServletResponse.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, csrfToken.getHeaderName());

            if (httpServletResponse.getHeaderNames().contains(csrfToken.getHeaderName()))
                httpServletResponse.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
            else
                httpServletResponse.addHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest httpServletRequest) {
        return (CsrfToken) httpServletRequest.getAttribute(CsrfToken.class.getName());
    }

    public void clearToken(HttpServletResponse response) {
        if (response.getHeaderNames().contains("x-csrf-token"))
            response.setHeader("x-csrf-token", "");
    }
}
