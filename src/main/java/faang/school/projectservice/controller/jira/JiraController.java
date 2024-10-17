package faang.school.projectservice.controller.jira;

import faang.school.projectservice.model.dto.jira.JiraDto;
import faang.school.projectservice.service.jira.JiraServiceImpl;
import faang.school.projectservice.validator.groups.ChangeStatusGroup;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.FilterGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import faang.school.projectservice.validator.groups.UpdateLinkGroup;
import jakarta.validation.constraints.NotBlank;
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
    private final JiraServiceImpl jiraService;

    @GetMapping("/issue1/{issueKey}")
    public Mono<Object> getIssue(@PathVariable @NotBlank String issueKey) {
        return jiraService.getIssue(issueKey);
    }

    @PostMapping("/task")
    public Mono<Object> createTask(@RequestBody @Validated(CreateGroup.class) JiraDto dto) {
        return jiraService.createTask(dto);
    }

    @PutMapping("/task")
    public Mono<Object> updateTask(@RequestBody @Validated(UpdateGroup.class) JiraDto dto) {
        return jiraService.updateTask(dto);
    }

    @PutMapping("/link")
    public Mono<Object> updateTaskLink(@RequestBody @Validated(UpdateLinkGroup.class) JiraDto dto) {
        return jiraService.updateTaskLink(dto);
    }

    @PutMapping("/status")
    public Mono<String> changeTaskStatus(@RequestBody @Validated(ChangeStatusGroup.class) JiraDto dto) {
        return jiraService.changeTaskStatus(dto);
    }

    @GetMapping("/tasks/{keyProject}")
    public Mono<Object> getAllTasks(@PathVariable @NotBlank String keyProject) {
        return jiraService.getAllTasks(keyProject);
    }

    @PostMapping("/filters")
    public Mono<JiraDto> getTaskByFilters(@RequestBody @Validated(FilterGroup.class) JiraDto dto) {
        return jiraService.getTaskByFilters(dto);
    }
}
