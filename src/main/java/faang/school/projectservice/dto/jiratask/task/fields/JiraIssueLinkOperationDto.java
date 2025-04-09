package faang.school.projectservice.dto.jiratask.task.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation.JiraLinkOperationDto;

public record JiraIssueLinkOperationDto(

        @JsonProperty("add")
        JiraLinkOperationDto add
) {
}
