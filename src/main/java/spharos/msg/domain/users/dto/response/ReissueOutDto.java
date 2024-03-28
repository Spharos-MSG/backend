package spharos.msg.domain.users.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReissueOutDto {

    private String refreshToken;
    private String accessToken;

    @Builder
    public ReissueOutDto(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
