package faang.school.projectservice.dto.jira;

import jakarta.validation.constraints.Future;
import lombok.Builder;
import lombok.NonNull;
import org.joda.time.LocalDateTime;

@Builder
public record IssueDto(String issueKey,
                       long typeId,
                       IssueTypeDto issueType,
                       @NonNull
                       String summary,
                       String description,
                       @Future
                       LocalDateTime dueDate) {
}