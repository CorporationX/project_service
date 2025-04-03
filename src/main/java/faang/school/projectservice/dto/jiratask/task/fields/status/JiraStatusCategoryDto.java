package faang.school.projectservice.dto.jiratask.task.fields.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraStatusCategoryDto(
        @JsonProperty("key") String key
) {
}
