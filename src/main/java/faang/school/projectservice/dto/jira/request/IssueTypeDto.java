package faang.school.projectservice.dto.jira.request;

import lombok.Data;

@Data
public class IssueTypeDto {
    private Long id;
    private String description;
    private String name;
    private boolean subtask;
}
