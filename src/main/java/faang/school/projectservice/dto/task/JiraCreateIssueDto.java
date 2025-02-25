package faang.school.projectservice.dto.task;

import lombok.Builder;

@Builder
public record JiraCreateIssueDto (
        String summary,
        String description,
        String username,
        Long issueType
) {}
