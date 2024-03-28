package spharos.msg.domain.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserOAuthList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_oauth_list_id")
    private Long id;

    @Column(name = "oauth_id")
    private String OAuthId;

    @Column(name = "oauth_name")
    private String OAuthName;

    @Column(name = "uuid")
    private String uuid;

    @Builder
    public UserOAuthList(String OAuthId, String OAuthName, String uuid) {
        this.OAuthId = OAuthId;
        this.OAuthName = OAuthName;
        this.uuid = uuid;
    }
}
