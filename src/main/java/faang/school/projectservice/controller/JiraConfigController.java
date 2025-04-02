package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.jira.JiraProperties;
import faang.school.projectservice.service.JiraConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JiraConfigController {

    private final JiraConfigService configService;

    @PostMapping("/jira/config")
    public ResponseEntity<String> setConfig(@RequestBody JiraProperties config) {
        configService.addJiraConfig(config);
        return ResponseEntity.ok("Config saved successfully");
    }
}
