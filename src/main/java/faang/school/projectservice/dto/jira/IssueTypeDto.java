package faang.school.projectservice.dto.jira;

import lombok.Builder;

@Builder
public record IssueTypeDto(Long id,
                           String name,
                           boolean isSubtask,
                           String description) {
}
