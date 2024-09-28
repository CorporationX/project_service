package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.JiraDto;
import faang.school.projectservice.service.jira.JiraService;
import faang.school.projectservice.validator.groups.ChangeStatusGroup;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.FilterGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import faang.school.projectservice.validator.groups.UpdateLinkGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/jira")
@RequiredArgsConstructor
public class JiraController {
    private final JiraService jiraService;

    @GetMapping("/issue1/{issueKey}")
    public Mono<Object> getIssueString(@PathVariable String issueKey) {
        return jiraService.getIssueString(issueKey);
    }

    @PostMapping("/create")
    public Mono<Object> createJiraTask(@RequestBody @Validated(CreateGroup.class) JiraDto dto) {
        return jiraService.createTask(dto);
    }

    @PutMapping("/update-task")
    public Mono<Object> updateJiraTask(@RequestBody @Validated(UpdateGroup.class) JiraDto dto) {
        return jiraService.updateTask(dto);
    }

    @PutMapping("/update-link")
    public Mono<Object> updateJiraLink(@RequestBody @Validated(UpdateLinkGroup.class) JiraDto dto) {
        return jiraService.updateTaskLink(dto);
    }

    @PutMapping("/change")
    public Mono<String> changeTaskStatus(@RequestBody @Validated(ChangeStatusGroup.class) JiraDto dto) {
        return jiraService.changeTaskStatus(dto);
    }

    @GetMapping("/tasks/{keyProject}")
    public Mono<Object> getAllTasks(@PathVariable String keyProject) {
        return jiraService.getAllTasks(keyProject);
    }

    @GetMapping("/filters")
    public Mono<JiraDto> getTaskByFilters(@RequestBody @Validated(FilterGroup.class) JiraDto dto) {
        return jiraService.getTaskByFilters(dto);
    }
}
