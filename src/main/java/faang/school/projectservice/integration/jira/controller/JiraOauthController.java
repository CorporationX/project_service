package faang.school.projectservice.integration.jira.controller;

import faang.school.projectservice.integration.jira.dto.response.OauthStatusResponse;
import faang.school.projectservice.integration.jira.Oauth.OauthStateManager;
import faang.school.projectservice.integration.jira.service.JiraOauthService;
import faang.school.projectservice.integration.jira.service.JiraTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * STATELESS Controller для Oauth 2.0 flow с Jira
 *
 * Работает в микросервисной архитектуре с Load Balancer:
 * - НЕ использует HttpSession (stateless)
 * - State хранится в Redis (shared state)
 * - Callback может прийти на любой инстанс за LB
 */
@Slf4j
@RestController
@RequestMapping("/api/jira/Oauth")
@RequiredArgsConstructor
public class JiraOauthController {
    private final OauthStateManager OauthStateManager;
    private final JiraTokenService jiraTokenService;
    private final JiraOauthService jiraOauthService;

    /**
     * Инициирует Oauth flow
     * Redirect пользователя на Jira authorization page
     *
     * GET /api/jira/Oauth/authorize?userId=123
     *
     * STATELESS: State сохраняется в Redis, а не в сессии
     */
    @GetMapping("/authorize")
    public RedirectView authorize(@RequestParam Long userId) {
        log.info("Starting Oauth authorization for user: {}", userId);

        try {
            String state = jiraOauthService.generateState();
            OauthStateManager.saveState(state, userId);
            log.debug("Generated state for user {}: {}", userId, state);

            String authorizationUrl = jiraOauthService.generateAuthorizationUrl(state);
            log.info("Redirecting user {} to Jira authorization", userId);

            return new RedirectView(authorizationUrl);

        } catch (Exception e) {
            log.error("Failed to start Oauth authorization for user: {}", userId, e);
            return new RedirectView("/Oauth/error?message=" + e.getMessage());
        }
    }

    /**
     * Callback от Jira после авторизации
     * Jira redirect сюда с authorization code
     *
     * GET /api/jira/Oauth/callback?code=AUTH_CODE&state=STATE
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
        log.info("Received Oauth callback with state: {}", state);
        log.debug("Authorization code: {}", code);

        try {

            Long userId = OauthStateManager.getUserIdByState(state);

            if (userId == null) {
                log.error("Invalid or expired state: {}", state);
                return new RedirectView("/Oauth/error?message=Invalid or expired state");
            }

            log.info("State validated successfully for user: {}", userId);

            jiraOauthService.exchangeCodeForToken(code, userId);

            log.info("Oauth flow completed successfully for user: {}", userId);

            OauthStateManager.removeState(state);

            return new RedirectView("/Oauth/success");

        } catch (Exception e) {
            log.error("Oauth callback failed", e);
            return new RedirectView("/Oauth/error?message=" + e.getMessage());
        }
    }

    /**
     * Отключение Jira для пользователя
     * Удаляет Oauth токены
     *
     * POST /api/jira/Oauth/disconnect
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
     * GET /api/jira/Oauth/status?userId=123
     *
     * STATELESS: работает из любого инстанса
     */
    @GetMapping("/status")
    public ResponseEntity<OauthStatusResponse> getStatus(@RequestParam Long userId) {
        log.debug("Checking Oauth status for user: {}", userId);

        try {
            boolean isConnected = jiraTokenService.hasValidToken(userId);

            OauthStatusResponse response = OauthStatusResponse.builder()
                    .userId(userId)
                    .connected(isConnected)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to check Oauth status for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
