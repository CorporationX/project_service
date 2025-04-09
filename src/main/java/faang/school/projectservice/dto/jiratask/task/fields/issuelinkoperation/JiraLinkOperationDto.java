package faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation.linkoperation.JiraLinkTypeDto;
import faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation.linkoperation.JiraLinkedIssueDto;
import lombok.Builder;

@Builder
public record JiraLinkOperationDto(

        @JsonProperty("type")
        JiraLinkTypeDto type,

        @JsonProperty("outwardIssue")
        JiraLinkedIssueDto outwardIssue
) {
}
