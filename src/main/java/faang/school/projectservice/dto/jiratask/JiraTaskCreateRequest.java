package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.JiraCreateFieldsDto;
import jakarta.validation.Valid;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraTaskCreateRequest(
        @JsonProperty("fields") @Valid JiraCreateFieldsDto fields
) {
}
