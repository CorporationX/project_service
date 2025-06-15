package faang.school.projectservice.service.google;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import faang.school.projectservice.exception.InternalServerErrorException;
import faang.school.projectservice.service.GoogleAuthService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {
    @Value("${google.service-account.key-file-path}")
    private String serviceAccountKeyPath;

    private GoogleCredentials credentials;

    private static final List<String> CALENDAR_SCOPES = List.of("https://www.googleapis.com/auth/calendar");

    @PostConstruct
    private void initializeCredentials() throws IOException {
        log.debug("Initializing Google Credentials from path: {}", serviceAccountKeyPath);
        try (InputStream credentialsStream = new ClassPathResource(serviceAccountKeyPath).getInputStream()) {
            this.credentials = ServiceAccountCredentials.fromStream(credentialsStream).createScoped(CALENDAR_SCOPES);
            log.info("Google Credentials initialized successfully for scopes: {}", CALENDAR_SCOPES);
        } catch (IOException e) {
            log.error("Failed to initialize Google Credentials: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to initialize Google Credentials: %s".formatted(e.getMessage()));
        }
    }

    public synchronized String getAccessToken() throws IOException {
        if (this.credentials == null) {
            log.warn("GoogleCredentials was not initialized");
            initializeCredentials();
            if (this.credentials == null) {
                throw new InternalServerErrorException("GoogleCredentials could not be initialized.");
            }
        }
        log.debug("Attempting to refresh Google access token if expired...");
        this.credentials.refreshIfExpired();
        AccessToken accessToken = this.credentials.getAccessToken();

        if (accessToken == null || accessToken.getTokenValue() == null) {
            log.warn("Failed to retrieve access token after refresh attempt. Token is null.");
            log.info("Attempting a forceful refresh of Google access token...");
            this.credentials.refresh();
            accessToken = this.credentials.getAccessToken();
            if (accessToken == null || accessToken.getTokenValue() == null) {
                log.error("Forceful refresh also failed to retrieve access token.");
                throw new IOException("Failed to retrieve access token, token is null even after forceful refresh.");
            }
            log.info("Forceful refresh successful. New token obtained.");
        }

        if (log.isDebugEnabled() && accessToken.getExpirationTime() != null) {
            log.debug("Current Google access token expires at: {}", accessToken.getExpirationTime());
        }
        return accessToken.getTokenValue();
    }
}