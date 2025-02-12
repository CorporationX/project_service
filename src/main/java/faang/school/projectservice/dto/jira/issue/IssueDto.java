package faang.school.projectservice.dto.jira.issue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IssueDto {
    @NotNull
    private Long typeId;

    @NotBlank
    private String summary;

    private String description;

    private String assigneeName;
}
