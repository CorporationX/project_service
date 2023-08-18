package faang.school.projectservice.controller;

import faang.school.projectservice.dto.jira.CreateJiraDto;
import faang.school.projectservice.dto.jira.ResponseJiraDto;
import faang.school.projectservice.service.JiraApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jira")
@RequiredArgsConstructor
public class JiraApiController {
    private final JiraApiService jiraApiService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseJiraDto connectJira(@RequestBody CreateJiraDto createJiraDto) {
        return jiraApiService.connectJira(createJiraDto);
    }
}
