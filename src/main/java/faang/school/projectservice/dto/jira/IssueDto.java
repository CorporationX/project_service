package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueDto {

    private String key;

    @NotNull(message = "Project key should not be null")
    @NotBlank(message = "Project key should not be blank")
    private String projectKey;

    @NotNull(message = "Issue type is required")
    private IssueTypeDto issueType;

    @NotNull(message = "Summary should not be null")
    @NotBlank(message = "Summary should not be blank")
    private String summary;

    @NotNull(message = "Description should not be null")
    @NotBlank(message = "Description should not be blank")
    private String description;

    private StatusDto status;

    private LocalDateTime creationDate;

    @JsonInclude(Include.NON_NULL)
    private LocalDateTime updateDate;

    @JsonInclude(Include.NON_NULL)
    private LocalDateTime dueDate;
}
