package spharos.msg.domain.users.service;

import spharos.msg.domain.users.dto.request.DuplicationCheckRequestDto;
import spharos.msg.domain.users.dto.request.LoginRequestDto;
import spharos.msg.domain.users.dto.response.ReissueOutDto;
import spharos.msg.domain.users.dto.request.SignUpRequestDto;
import spharos.msg.domain.users.dto.response.LoginOutDto;

public interface AuthService {

    void signUp(SignUpRequestDto signUpRequestDto);

    LoginOutDto login(LoginRequestDto loginRequestDto);

    void logout(String uuid);

    ReissueOutDto reissueToken(String token);

    void duplicateCheckLoginId(DuplicationCheckRequestDto duplicationCheckRequestDto);
}
