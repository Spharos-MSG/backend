package spharos.msg.domain.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spharos.msg.domain.users.dto.request.EasyLoginRequestDto;
import spharos.msg.domain.users.dto.request.EasySignUpRequestDto;
import spharos.msg.domain.users.dto.response.LoginOutDto;
import spharos.msg.domain.users.entity.UserOAuthList;
import spharos.msg.domain.users.entity.Users;
import spharos.msg.domain.users.repository.UserOAuthListRepository;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.global.api.code.status.ErrorStatus;
import spharos.msg.global.api.exception.UsersException;
import spharos.msg.global.security.JwtTokenProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final UsersRepository userRepository;
    private final UserOAuthListRepository userOAuthListRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginOutDto easySignUp(EasySignUpRequestDto easySignUpRequestDto) {
        Users users = userRepository.findByEmail(easySignUpRequestDto.getEmail()).orElseThrow(
                () -> new UsersException(ErrorStatus.NOT_UNION_USER)
        );

        if (Boolean.TRUE.equals(userOAuthListRepository.existsByUuid(users.getUuid()))) {
            log.info("기존 가입된 회원이라 바로 로그인 처리");
            return easyLogin(EasyLoginRequestDto
                    .builder()
                    .OAuthId(easySignUpRequestDto.getOauth_id())
                    .OAuthName(easySignUpRequestDto.getOauth_name())
                    .build());
        } else {
            UserOAuthList userOAuthList = UserOAuthList
                    .builder()
                    .OAuthId(easySignUpRequestDto.getOauth_id())
                    .OAuthName(easySignUpRequestDto.getOauth_name())
                    .uuid(users.getUuid())
                    .build();

            userOAuthListRepository.save(userOAuthList);
        }
        return null;
    }

    @Override
    public LoginOutDto easyLogin(EasyLoginRequestDto easyLoginRequestDto) {
        UserOAuthList userOAuthList = userOAuthListRepository.findByOAuthIdAndOAuthName(
                easyLoginRequestDto.getOAuthId(),
                easyLoginRequestDto.getOAuthName()).orElseThrow(
                () -> new UsersException(ErrorStatus.LOG_IN_EASY_FAIL));

        Users findUser = userRepository.findByUuid(userOAuthList.getUuid()).orElseThrow(
                () -> new UsersException(ErrorStatus.NOT_UNION_USER));

        //todo : 인증처리 생각 할 것.
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        findUser.getUsername(),
//                        loginRequestDto.getPassword()
//                )
//        );

        //토큰생성
        String refreshToken = jwtTokenProvider.createRefreshToken(findUser);
        String accessToken = jwtTokenProvider.createAccessToken(findUser);

        return LoginOutDto
                .builder()
                .uuid(findUser.getUuid())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .name(findUser.readUserName())
                .email(findUser.getEmail())
                .build();
    }
}
