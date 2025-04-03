package faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation.linkoperation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JiraLinkTypeDto(

        @JsonProperty("name")
        String name
) {
}
