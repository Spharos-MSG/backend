package spharos.msg.domain.users.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAuthRequestDto {

    String email;
    String secretKey;
}
