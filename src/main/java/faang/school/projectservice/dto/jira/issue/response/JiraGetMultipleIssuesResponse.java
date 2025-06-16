package faang.school.projectservice.dto.jira.issue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraGetMultipleIssuesResponse {
    private String expand; // Расширенные поля (опционально)
    private int startAt;   // Начальный индекс
    private int maxResults; // Максимум задач на странице
    private int total;      // Общее количество задач
    private List<JiraGetIssueResponseDto> issues; // Список задач
}
