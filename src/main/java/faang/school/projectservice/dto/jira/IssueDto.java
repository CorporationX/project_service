package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime creationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updateDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime dueDate;
}
