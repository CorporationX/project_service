package faang.school.projectservice.dto.jira;

import lombok.NonNull;

public record IssueFilterDto(
        @NonNull
        String projectKey,
        String status,
        String assignee) {
}
