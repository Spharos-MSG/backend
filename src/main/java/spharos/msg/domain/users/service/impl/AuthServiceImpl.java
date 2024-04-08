package spharos.msg.domain.users.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spharos.msg.domain.users.dto.request.AuthRequest;
import spharos.msg.domain.users.dto.response.AuthResponse;
import spharos.msg.domain.users.entity.UserStatus;
import spharos.msg.domain.users.entity.Users;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.domain.users.service.AuthService;
import spharos.msg.global.api.code.status.ErrorStatus;
import spharos.msg.global.api.exception.JwtTokenException;
import spharos.msg.global.api.exception.UsersException;
import spharos.msg.global.redis.RedisService;
import spharos.msg.global.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Transactional
    @Override
    public void signUp(AuthRequest.SignUpRequestDto dto) {

        //중복회원 검색
        duplicateCheckLoginId(AuthRequest.DuplicationCheckDto
                        .builder()
                        .loginId(dto.getLoginId())
                        .build());

        //탈퇴한 회원 검증.
        //한번 탈퇴하면 절대 회원가입/로그인 금지 되는 정책 적용 중.
        if (usersRepository.findByLoginId(dto.getLoginId())
                .filter(m -> m.getStatus() == UserStatus.NOT_USER).isPresent()) {
            throw new UsersException(ErrorStatus.WITHDRAW_USER_FAIL);
        }

        String uuid = UUID.randomUUID().toString();
        Users user = new Users(uuid);
        user.passwordToHash(dto.getPassword());

        usersRepository.save(Users
                .builder()
                .email(dto.getEmail())
                .userName(dto.getUsername())
                .loginId(dto.getLoginId())
                .phoneNumber(dto.getPhoneNumber())
                .password(user.getPassword())
                .uuid(user.getUuid())
                .address(dto.getAddress())
                .status(UserStatus.UNION)
                .build());
    }

    @Transactional(readOnly = true)
    @Override
    public AuthResponse.LoginResponseDto login(AuthRequest.LoginRequestDto dto) {
        Users findUser = usersRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new UsersException(ErrorStatus.LOG_IN_UNION_FAIL));

        //탈퇴 회원 검증 로직 추가
        if (findUser.getStatus().equals(UserStatus.NOT_USER)) {
            throw new UsersException(ErrorStatus.WITHDRAW_USER_FAIL);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        findUser.getUsername(),
                        dto.getPassword()
                )
        );

        //create token
        String accessToken = jwtTokenProvider.createAccessToken(findUser);
        String refreshToken = jwtTokenProvider.createRefreshToken(findUser);

        return AuthResponse.LoginResponseDto
                .builder()
                .userId(findUser.getId())
                .uuid(findUser.getUuid())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .name(findUser.readUserName())
                .email(findUser.getEmail())
                .build();
    }

    public void logout(Long userId) {
        Users findUser = usersRepository.findById(userId).orElseThrow();
        if (Boolean.TRUE.equals(redisService.isRefreshTokenExist(findUser.getUuid()))) {
            redisService.deleteRefreshToken(findUser.getUuid());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public AuthResponse.ReissueResponseDto reissueToken(String oldRefreshToken) {
        String uuid = jwtTokenProvider.getUuid(oldRefreshToken);
        String oldToken = oldRefreshToken.substring(7);

        Users findUser = usersRepository.findByUuid(uuid).orElseThrow(
                () -> new JwtTokenException(ErrorStatus.REISSUE_TOKEN_FAIL));

        if (!redisService.getRefreshToken(uuid).equals(oldToken)) {
            throw new JwtTokenException(ErrorStatus.REISSUE_TOKEN_FAIL);
        }

        String accessToken = jwtTokenProvider.createAccessToken(findUser);
        String refreshToken = jwtTokenProvider.createRefreshToken(findUser);
        return AuthResponse.ReissueResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public void duplicateCheckLoginId(AuthRequest.DuplicationCheckDto dto) {
        if (usersRepository.existsByLoginId(dto.getLoginId())) {
            throw new UsersException(ErrorStatus.DUPLICATION_LOGIN_ID);
        }
    }

    @Transactional
    @Override
    public void withdrawMember(Long userId) {
        Users findUser = usersRepository.findById(userId).orElseThrow(
                () -> new UsersException(ErrorStatus.NOT_USER)
        );
        usersRepository.save(Users
                .builder()
                .id(findUser.getId())
                .email(findUser.getEmail())
                .userName(findUser.readUserName())
                .loginId(findUser.getLoginId())
                .phoneNumber(findUser.getPhoneNumber())
                .password(findUser.getPassword())
                .uuid(findUser.getUuid())
                .address(findUser.getAddress())
                .status(UserStatus.NOT_USER)
                .build());
    }

    @Transactional
    @Override
    public void changePassword(AuthRequest.ChangePasswordDto dto) {
        Users findUser = usersRepository.findByLoginId(dto.getLoginId())
                .orElseThrow();

        if (validatePassword(findUser.getPassword(),
                dto.getModifyPassword())) {
            throw new UsersException(ErrorStatus.SAME_PASSWORD);
        }

        usersRepository.save(Users
                .builder()
                .id(findUser.getId())
                .loginId(findUser.getLoginId())
                .uuid(findUser.getUuid())
                .password(hashPassword(dto.getModifyPassword()))
                .phoneNumber(findUser.getPhoneNumber())
                .email(findUser.getEmail())
                .userName(findUser.readUserName())
                .address(findUser.getAddress())
                .status(findUser.getStatus())
                .build());
    }

    private Boolean validatePassword(String oldPasswordHash, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(newPassword, oldPasswordHash);
    }

    private String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }


    @Transactional(readOnly = true)
    @Override
    public AuthResponse.FindIdResponseDto findLoginUnionId(String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(
                () -> new UsersException(ErrorStatus.FIND_LOGIN_ID_FAIL));

        return AuthResponse.FindIdResponseDto
                .builder()
                .loginId(user.getLoginId())
                .build();
    }

    @Override
    public AuthResponse.FindUserInfoResponseDto findUserInfo(String uuid) {
        Users findUser = usersRepository.findByUuid(uuid).orElseThrow(
                () -> new UsersException(ErrorStatus.FIND_USER_INFO_FAIL)
        );

        return AuthResponse.FindUserInfoResponseDto
                .builder()
                .userName(findUser.readUserName())
                .email(findUser.getEmail())
                .phoneNumber(findUser.getPhoneNumber())
                .build();
    }
}