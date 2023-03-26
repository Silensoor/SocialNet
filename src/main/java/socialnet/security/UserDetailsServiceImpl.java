package socialnet.security;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import socialnet.model.Person;

import java.util.Objects;

@Component

public class UserDetailsServiceImpl implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public UserDetailsServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Person user = jdbcTemplate.query("SELECT * FROM persons where email=?",new Object[]{email},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);


        return UserDetailsImpl.build(Objects.requireNonNull(user));
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
