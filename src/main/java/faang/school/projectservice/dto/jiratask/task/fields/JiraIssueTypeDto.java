package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record JiraIssueTypeDto(
        @JsonProperty("name") @NotBlank String name
) {
}
