package socialnet.service.login;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.naming.AuthenticationException;

public interface AuthenticationManager {
  Authentication authenticate(UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException;
}
