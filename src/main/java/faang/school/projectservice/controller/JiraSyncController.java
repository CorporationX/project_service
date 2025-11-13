package faang.school.projectservice.controller;

import faang.school.projectservice.integration.jira.service.JiraIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/jira/sync")
@RequiredArgsConstructor
@Tag(name = "Jira Sync", description = "Синхронизация с Jira")
public class JiraSyncController {
    
    private final JiraIntegrationService integrationService;
    
    // ==========================================
    // Sync Operations
    // ==========================================
    
    @Operation(
        summary = "Синхронизировать проект",
        description = "Запускает полную синхронизацию проекта с Jira"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Синхронизация завершена"),
        @ApiResponse(responseCode = "500", description = "Ошибка синхронизации")
    })
    @PostMapping("/project/{projectId}")
    public ResponseEntity<JiraIntegrationService.SyncResult> syncProject(
        @PathVariable Long projectId
    ) {
        log.info("Starting sync for project: {}", projectId);
        
        JiraIntegrationService.SyncResult result = integrationService.syncProject(projectId);
        
        return ResponseEntity.ok(result);
    }
    
    // ==========================================
    // Status & Health
    // ==========================================
    
    @Operation(
        summary = "Статус интеграции",
        description = "Возвращает текущий статус Jira интеграции"
    )
    @GetMapping("/status")
    public ResponseEntity<JiraIntegrationService.IntegrationStatus> getStatus() {
        log.info("Getting integration status");
        
        JiraIntegrationService.IntegrationStatus status = integrationService.getIntegrationStatus();
        
        return ResponseEntity.ok(status);
    }
    
    @Operation(
        summary = "Health check",
        description = "Проверяет доступность Jira"
    )
    @GetMapping("/health")
    public ResponseEntity<HealthStatus> checkHealth() {
        boolean healthy = integrationService.checkJiraHealth();
        
        HealthStatus status = new HealthStatus();
        status.setHealthy(healthy);
        status.setMessage(healthy ? "Jira is available" : "Jira is unavailable");
        
        return ResponseEntity.ok(status);
    }
    
    @Data
    static class HealthStatus {
        private boolean healthy;
        private String message;
    }
}

