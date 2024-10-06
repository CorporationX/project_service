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

    @Operation(summary = "Handle OAuth2 Callback",
            description = "Processes the OAuth2 callback using the provided authorization code.")
    @GetMapping("/oauth/callback")
    public ResponseEntity<String> callback(@RequestParam(value = "code", required = false) String code,
                                           @RequestParam(value = "error", required = false) String error) {
        String result = oAuthService.processOAuthCallback(code, error);
        return ResponseEntity.ok(result);
    }
}