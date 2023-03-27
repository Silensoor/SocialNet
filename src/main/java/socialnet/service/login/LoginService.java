package socialnet.service.login;

import socialnet.dto.LoginRq;

public interface LoginService {

    Object getLogin(LoginRq loginRq);
//    Object getMe (String getToken);
}
