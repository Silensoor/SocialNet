package socialnet.security;

import liquibase.pro.packaged.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    public UserService (UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public List<User> getAll(){
        return userRepository.findAll();
    }
    public User getByEmail(String email){
        return userRepository.findByEmail(email);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getByEmail(email);
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException(String.format("Email %s is not found", email));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true, true, true, true, new HashSet<>());
    }
}
