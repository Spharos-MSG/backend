package spharos.msg.domain.users.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailOutDto {
    String secretKey;

    @Builder
    public EmailOutDto(String secretKey) {
        this.secretKey = secretKey;
    }
}
