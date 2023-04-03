package socialnet.service.login;

import socialnet.api.request.LoginRq;

public interface LoginService {

    Object getLogin(LoginRq loginRq);

    Object getMe(String authorization);

    Object getLogout(String authorization);
}
