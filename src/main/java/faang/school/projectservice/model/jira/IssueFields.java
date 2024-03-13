package faang.school.projectservice.model.jira;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueFields {
    private Project project;
    private Description description;
    private String summary;
    private IssueType issuetype;
    private IssueAssignee assignee;
    private String duedate;
    private Issue parent;
}
