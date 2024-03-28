package spharos.msg.domain.users.service;

import spharos.msg.domain.users.dto.request.EasyLoginRequestDto;
import spharos.msg.domain.users.dto.request.EasySignUpRequestDto;
import spharos.msg.domain.users.dto.response.LoginOutDto;

public interface OAuthService {

    LoginOutDto easySignUp(EasySignUpRequestDto easySignUpRequestDto);

    LoginOutDto easyLogin(EasyLoginRequestDto easyLoginRequestDto);
}
