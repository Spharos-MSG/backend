package spharos.msg.domain.users.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import spharos.msg.domain.users.dto.request.EmailAuthRequestDto;
import spharos.msg.domain.users.dto.request.EmailSendRequestDto;
import spharos.msg.domain.users.dto.response.EmailOutDto;
import spharos.msg.domain.users.service.UsersService;
import spharos.msg.global.api.ApiResponse;
import spharos.msg.global.api.code.status.SuccessStatus;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "회원 관련 API")
public class UsersController {

    private final UsersService usersService;

    @Operation(summary = "이메일 발송",
            description = "이메일 중복 확인 후, 이메일 인증을 위한 이메일을 발송 합니다.")
    @PostMapping("/send-mail")
    public ApiResponse<EmailOutDto> sendEmail(
            @RequestBody EmailSendRequestDto emailSendRequestDto
    ) {
        usersService.duplicateCheckEmail(emailSendRequestDto);
        EmailOutDto emailOutDto = usersService.sendMail(emailSendRequestDto);
        return ApiResponse.of(SuccessStatus.EMAIL_SEND_SUCCESS, emailOutDto);
    }

    @Operation(summary = "이메일 인증 확인",
            description = "입력받은 SecretKey로 인증을 진행 합니다.")
    @PostMapping("/authenticate-email")
    public ApiResponse<?> authenticateEmail(
            @RequestBody EmailAuthRequestDto emailAuthRequestDto
    ) {
        usersService.authenticateEmail(emailAuthRequestDto);
        return ApiResponse.of(SuccessStatus.EMAIL_AUTH_SUCCESS, null);
    }

    //todo: 회원삭제. 회원 삭제시, OAuth 사용자도 삭제 처리 필요
}
