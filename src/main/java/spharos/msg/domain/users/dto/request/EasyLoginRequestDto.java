package spharos.msg.domain.users.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EasyLoginRequestDto {

    private String OAuthId;
    private String OAuthName;

    @Builder
    public EasyLoginRequestDto(String OAuthId, String OAuthName) {
        this.OAuthId = OAuthId;
        this.OAuthName = OAuthName;
    }
}
