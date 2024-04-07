package faang.school.projectservice.dto.jira;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueDto {
    private String key;
    @NotBlank(message = "Project key is required")
    private String projectKey;
    @NotNull(message = "Issue type is required")
    private IssueTypeDto issueType;
    @NotBlank(message = "Summary is required")
    private String summary;
    @NotBlank(message = "Description is required")
    private String description;
    private StatusDto status;
}
