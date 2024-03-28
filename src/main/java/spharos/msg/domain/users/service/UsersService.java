package spharos.msg.domain.users.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spharos.msg.domain.users.dto.request.EmailAuthRequestDto;
import spharos.msg.domain.users.dto.request.EmailSendRequestDto;
import spharos.msg.domain.users.dto.response.EmailOutDto;
import spharos.msg.domain.users.repository.UsersRepository;
import spharos.msg.global.api.code.status.ErrorStatus;
import spharos.msg.global.api.exception.UsersException;
import spharos.msg.global.redis.RedisService;


@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final UsersRepository userRepository;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.expiration}")
    private long expiration;
    @Value("${spring.mail.stringSize}")
    private int stringSize;

    public EmailOutDto sendMail(EmailSendRequestDto emailSendRequestDto) {
        MimeMessage message = mailSender.createMimeMessage();
        String secretKey = createKey();
        try {
            String email = emailSendRequestDto.getEmail();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("MSG.COM 회원 가입 인증 메일 입니다");
            mimeMessageHelper.setText("인증번호 : " + secretKey);
            mailSender.send(message);

            if (redisService.isEmailSecretKeyExist(email)) {
                redisService.deleteEmailSecretKey(email);
            }
            redisService.saveEmailSecretKey(email, secretKey, expiration);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return EmailOutDto.builder().secretKey(secretKey).build();
    }

    private String createKey() {
        int leftLimit = '0';
        int rightLimit = 'z';
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(stringSize)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void authenticateEmail(EmailAuthRequestDto emailAuthRequestDto) {
        String findSecretKey = redisService.getEmailSecretKey(emailAuthRequestDto.getEmail());
        if (!Objects.equals(findSecretKey, emailAuthRequestDto.getSecretKey())) {
            throw new UsersException(ErrorStatus.EMAIL_VALIDATE_FAIL);
        }
        redisService.deleteEmailSecretKey(emailAuthRequestDto.getEmail());
    }

    //Email 중복 확인
    @Transactional(readOnly = true)
    public void duplicateCheckEmail(EmailSendRequestDto emailRequestDto) {
        if (userRepository.existsByEmail(emailRequestDto.getEmail())) {
            throw new UsersException(ErrorStatus.ALREADY_EXIST_EMAIL);
        }
    }
}
