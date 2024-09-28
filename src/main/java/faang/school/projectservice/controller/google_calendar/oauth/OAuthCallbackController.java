package faang.school.projectservice.controller.google_calendar.oauth;

import faang.school.projectservice.oauth.google_oauth.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "OAuth Callback", description = "OAuth2 Callback Controller")
public class OAuthCallbackController {

    private final OAuthService oAuthService;

    @Operation(summary = "Обработать OAuth2 Callback",
            description = "Обрабатывает OAuth2 callback, используя переданный код авторизации.")
    @GetMapping("/oauth/callback")
    public ResponseEntity<Void> callback(@RequestParam(value = "code", required = false) String code,
                                           @RequestParam(value = "error", required = false) String error) {
        oAuthService.processOAuthCallback(code, error);
        return ResponseEntity.ok().build();
    }
}