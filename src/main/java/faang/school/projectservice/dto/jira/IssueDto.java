package faang.school.projectservice.dto.jira;


import faang.school.projectservice.model.jira.Description;
import lombok.Data;

import java.util.List;

@Data
public class IssueDto {
    private String id;
    private String key;
    private String projectKey;
    private String duedate;
    private String issueTypeId;
    private String summary;
    private Description description;
    private String assigneeId;
    private String parentId;
    private List<String> subtaskIds;

}
