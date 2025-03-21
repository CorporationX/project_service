package faang.school.projectservice.dto.jira.request;

import lombok.Data;

@Data
public class IssueStatusRequestDto {
    private String id;
    private String description;
    private String name;
}
