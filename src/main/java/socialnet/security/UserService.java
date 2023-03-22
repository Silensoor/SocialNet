package socialnet.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService implements UserDetailsService {

    private final SocialNetUserRepository socialNetUserRepository;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        socialNetUserRepository.findByEmail(s);
    }
}
