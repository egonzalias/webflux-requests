package co.com.crediya.model.loanrequest.gateways;

import co.com.crediya.model.loanrequest.JwtUserInfo;
import co.com.crediya.model.loanrequest.User;

public interface TokenService {
    String generateToken(User user);
    boolean validateToken(String token);
    JwtUserInfo getUserInfoFromToken(String token);
}
