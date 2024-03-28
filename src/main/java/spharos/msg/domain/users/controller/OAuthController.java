package spharos.msg.domain.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spharos.msg.domain.users.dto.request.EasyLoginRequestDto;
import spharos.msg.domain.users.dto.request.EasySignUpRequestDto;
import spharos.msg.domain.users.dto.response.LoginOutDto;
import spharos.msg.domain.users.service.OAuthServiceImpl;
import spharos.msg.global.api.ApiResponse;
import spharos.msg.global.api.code.status.SuccessStatus;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
@Tag(name = "OAuth", description = "간편 로그인 관련 API")
public class OAuthController {

    private final OAuthServiceImpl oAuthService;

    @Operation(summary = "간편 회원가입", description = "간편회원 회원가입")
    @PostMapping("/signup/easy")
    public ApiResponse<?> signUpEasy(
            @RequestBody EasySignUpRequestDto easySignUpRequestDto
    ) {
        LoginOutDto login = oAuthService.easySignUp(easySignUpRequestDto);
        return ApiResponse.of(SuccessStatus.SIGN_UP_SUCCESS_EASY, login);
    }

    @Operation(summary = "간편 회원로그인", description = "간편회원 로그인")
    @PostMapping("/login/easy")
    public ApiResponse<?> loginEasy(
            @RequestBody EasyLoginRequestDto easyLoginRequestDto
    ) {
        LoginOutDto login = oAuthService.easyLogin(easyLoginRequestDto);
        return ApiResponse.of(SuccessStatus.LOGIN_SUCCESS_EASY, login);
    }
}
