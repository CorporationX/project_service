package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.jira.JiraProperties;
import faang.school.projectservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JiraConfigServiceTest {

    private final Long userId = 1L;

    @InjectMocks
    private JiraConfigService jiraConfigService;

    @Mock
    private UserContext userContext;

    @Mock
    private CryptoService cryptoService;

    @Test
    void testPositiveAddConfig() {
        JiraProperties properties = createProperties();
        String encryptedToken = "encrypted-test-token";
        when(userContext.getUserId()).thenReturn(userId);
        when(cryptoService.encrypt(properties.getApiToken())).thenReturn(encryptedToken);

        jiraConfigService.addJiraConfig(properties);

        verify(cryptoService, times(1)).encrypt(any());
        assertNotNull(properties.getUsedAt());
    }

    @Test
    void testNegativeGetConfig() {
        assertThrows(EntityNotFoundException.class, () -> jiraConfigService.getJiraConfig());
    }

    @Test
    void testPositiveGetConfig() {
        JiraProperties properties = createProperties();
        String encryptedToken = "encrypted-test-token";
        String decryptedToken = "decrypted-test-token";
        when(userContext.getUserId()).thenReturn(userId);
        when(cryptoService.encrypt(properties.getApiToken())).thenReturn(encryptedToken);
        jiraConfigService.addJiraConfig(properties);
        when(cryptoService.decrypt(encryptedToken)).thenReturn(decryptedToken);

        JiraProperties result = jiraConfigService.getJiraConfig();

        verify(cryptoService, times(1)).decrypt(any());
        assertEquals(properties.getBaseUrl(), result.getBaseUrl());
        assertEquals(properties.getEmail(), result.getEmail());
        assertNotNull(result.getUsedAt());
    }

    @Test
    void testPositiveClearOldConfigs() {
        Instant now = Instant.now();
        Instant oldTime = now.minusSeconds(60 * 60 * 2);
        JiraProperties properties = createProperties();
        String encryptedToken = "encrypted-test-token";
        properties.setUsedAt(oldTime);
        when(userContext.getUserId()).thenReturn(userId);
        when(cryptoService.encrypt(properties.getApiToken())).thenReturn(encryptedToken);
        jiraConfigService.addJiraConfig(properties);

        jiraConfigService.clearOldJiraConfigs();
        when(jiraConfigService.getJiraConfig()).thenThrow(EntityNotFoundException.class);

        verify(cryptoService, times(1)).encrypt(any());
        assertThrows(EntityNotFoundException.class, () -> jiraConfigService.getJiraConfig());
    }

    private JiraProperties createProperties() {
        return JiraProperties.builder()
                .baseUrl("https://test.atlassian.net/")
                .email("test@test.com")
                .apiToken("test-token")
                .build();
    }
}
