package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.status.JiraStatusCategoryDto;

public record JiraStatusDto(
        @JsonProperty("name") String name,
        @JsonProperty("statusCategory") JiraStatusCategoryDto statusCategory
) {
}
