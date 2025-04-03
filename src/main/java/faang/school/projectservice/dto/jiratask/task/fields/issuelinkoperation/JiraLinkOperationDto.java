package faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation.linkoperation.JiraLinkTypeDto;
import faang.school.projectservice.dto.jiratask.task.fields.issuelinkoperation.linkoperation.JiraLinkedIssueDto;

public record JiraLinkOperationDto(

        @JsonProperty("type")
        JiraLinkTypeDto type,

        @JsonProperty("outwardIssue")
        JiraLinkedIssueDto outwardIssue
) {
}
