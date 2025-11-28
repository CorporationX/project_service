package faang.school.projectservice.integration.jira.controller;

import faang.school.projectservice.integration.jira.dto.response.OAuthStatusResponse;
import faang.school.projectservice.integration.jira.oauth.OAuthStateManager;
import faang.school.projectservice.integration.jira.service.JiraOAuthService;
import faang.school.projectservice.integration.jira.service.JiraTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * STATELESS Controller для OAuth 2.0 flow с Jira
 *
 * Работает в микросервисной архитектуре с Load Balancer:
 * - НЕ использует HttpSession (stateless)
 * - State хранится в Redis (shared state)
 * - Callback может прийти на любой инстанс за LB
 */
@Slf4j
@RestController
@RequestMapping("/api/jira/oauth")
@RequiredArgsConstructor
public class JiraOAuthController {
    private final OAuthStateManager oauthStateManager;
    private final JiraTokenService jiraTokenService;
    private final JiraOAuthService jiraOAuthService;

    /**
     * Инициирует OAuth flow
     * Redirect пользователя на Jira authorization page
     *
     * GET /api/jira/oauth/authorize?userId=123
     *
     * STATELESS: State сохраняется в Redis, а не в сессии
     */
    @GetMapping("/authorize")
    public RedirectView authorize(@RequestParam Long userId) {
        log.info("Starting OAuth authorization for user: {}", userId);

        try {
            String state = jiraOAuthService.generateState();
            oauthStateManager.saveState(state, userId);
            log.debug("Generated state for user {}: {}", userId, state);

            String authorizationUrl = jiraOAuthService.generateAuthorizationUrl(state);
            log.info("Redirecting user {} to Jira authorization", userId);

            return new RedirectView(authorizationUrl);

        } catch (Exception e) {
            log.error("Failed to start OAuth authorization for user: {}", userId, e);
            return new RedirectView(String.format("/oauth/error?message=%s", e.getMessage()));
        }
    }

    /**
     * Callback от Jira после авторизации
     * Jira redirect сюда с authorization code
     *
     * GET /api/jira/oauth/callback?code=AUTH_CODE&state=STATE
     *
     * STATELESS:
     * - Запрос может прийти на любой инстанс за Load Balancer
     * - State достаётся из Redis (shared storage)
     */
    @GetMapping("/callback")
    public RedirectView callback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        log.info("Received OAuth callback with state: {}", state);
        log.debug("Authorization code: {}", code);

        try {

            Long userId = oauthStateManager.getUserIdByState(state);

            if (userId == null) {
                log.error("Invalid or expired state: {}", state);
                return new RedirectView("/oauth/error?message=Invalid or expired state");
            }

            log.info("State validated successfully for user: {}", userId);

            jiraOAuthService.exchangeCodeForToken(code, userId);

            log.info("OAuth flow completed successfully for user: {}", userId);

            oauthStateManager.removeState(state);

            return new RedirectView("/oauth/success");

        } catch (Exception e) {
            log.error("OAuth callback failed", e);
            return new RedirectView(String.format("/oauth/error?message=%s", e.getMessage()));
        }
    }

    /**
     * Отключение Jira для пользователя
     * Удаляет OAuth токены
     *
     * POST /api/jira/oauth/disconnect
     *
     * STATELESS: работает из любого инстанса
     */
    @PostMapping("/disconnect")
    public ResponseEntity<Void> disconnect(@RequestParam Long userId) {
        log.info("Disconnecting Jira for user: {}", userId);

        try {
            jiraTokenService.deleteToken(userId);
            log.info("Jira disconnected for user: {}", userId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Failed to disconnect Jira for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Проверка статуса подключения к Jira
     *
     * GET /api/jira/oauth/status?userId=123
     *
     * STATELESS: работает из любого инстанса
     */
    @GetMapping("/status")
    public ResponseEntity<OAuthStatusResponse> getStatus(@RequestParam Long userId) {
        log.debug("Checking OAuth status for user: {}", userId);

        try {
            boolean isConnected = jiraTokenService.hasValidToken(userId);

            OAuthStatusResponse response = OAuthStatusResponse.builder()
                    .userId(userId)
                    .connected(isConnected)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to check OAuth status for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
