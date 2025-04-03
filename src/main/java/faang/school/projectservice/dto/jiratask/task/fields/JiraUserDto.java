package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraUserDto(

        @JsonProperty("accountId")
        String accountId,

        @JsonProperty("displayName")
        String displayName
) {
}
