package faang.school.projectservice.model.google;

import com.google.api.client.auth.oauth2.StoredCredential;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "google_tokens")
@AllArgsConstructor
@NoArgsConstructor
public class GoogleToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "oauth_client_id")
    private String oauthClientId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expiration")
    private Long expirationTimeMills;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_id")
    private Long userId;

    public StoredCredential toStoredCredential() {
        return new StoredCredential()
                .setAccessToken(this.accessToken)
                .setRefreshToken(this.refreshToken)
                .setExpirationTimeMilliseconds(this.expirationTimeMills);
    }
}