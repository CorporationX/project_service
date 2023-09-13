package faang.school.projectservice.dto.jira;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueFilterDto {

    private String project;

    private String assignee;

    @Pattern(regexp = "^(?i)(to do|in progress|done)$",
            message = "Status must be one of the following: to do, in progress or done")
    private String status;
}
