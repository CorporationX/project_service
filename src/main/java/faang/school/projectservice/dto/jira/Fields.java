package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.dto.jira.filter.AssigneeDto;
import faang.school.projectservice.dto.jira.request.IssueStatusRequestDto;
import faang.school.projectservice.dto.jira.request.IssueTypeDto;
import faang.school.projectservice.dto.jira.request.ProjectRequestDto;
import faang.school.projectservice.dto.parent.ParentDto;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fields {
    private String summary;
    private String description;
    private IssueTypeDto issuetype;
    private ParentDto parent;
    private AssigneeDto assignee;
    private ProjectRequestDto project;
    private IssueStatusRequestDto status;
}
