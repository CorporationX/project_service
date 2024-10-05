package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.JiraDto;
import reactor.core.publisher.Mono;

public interface JiraService {
    Mono<Object> getIssue(String issueKey);

    Mono<Object> createTask(JiraDto dto);

    Mono<Object> updateTask(JiraDto dto);

    Mono<Object> updateTaskLink(JiraDto dto);

    Mono<String> changeTaskStatus(JiraDto dto);

    Mono<Object> getAllTasks(String keyProject);

    Mono<JiraDto> getTaskByFilters(JiraDto dto);
}
