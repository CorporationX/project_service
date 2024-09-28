package faang.school.projectservice.controller.google_calendar.oauth;

import faang.school.projectservice.service.google_calendar.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Google OAuth", description = "Google OAuth2 Controller")
public class GoogleOAuthController {

    private final OAuthService oAuthService;

    @Operation(summary = "Получить URL для авторизации", description = "Возвращает URL для авторизации Google OAuth2.")
    @GetMapping("/oauth/redirect")
    public ResponseEntity<String> oauthRedirect() {
        String authorizationUrl = oAuthService.buildAuthorizationUrl();
        log.info("Возвращение URL для авторизации: {}", authorizationUrl);
        return ResponseEntity.ok(authorizationUrl);
    }
}