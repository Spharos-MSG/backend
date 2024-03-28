package spharos.msg.domain.users.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendRequestDto {

    @Email
    private String email;
}
