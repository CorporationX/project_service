package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record JiraProjectDto(
        @JsonProperty("key") @NotBlank String key
) {
}
