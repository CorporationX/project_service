package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.JiraUpdateDto;
import faang.school.projectservice.dto.jiratask.task.JiraUpdateFieldsDto;
import jakarta.validation.Valid;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraTaskUpdateRequest(
        @JsonProperty("fields") @Valid JiraUpdateFieldsDto fields,
        @JsonProperty("update") JiraUpdateDto update
) {
}
