package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record JiraUserDto(

        @JsonProperty("accountId")
        String accountId,

        @JsonProperty("displayName")
        String displayName
) {
}
