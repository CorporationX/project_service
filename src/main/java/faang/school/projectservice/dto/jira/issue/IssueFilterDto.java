package faang.school.projectservice.dto.jira.issue;

import lombok.Data;

@Data
public class IssueFilterDto {
    private Long typeIdPattern;
    private String summaryPattern;
    private String descriptionPattern;
    private String assigneeNamePattern;
    private String reporterNamePattern;
}
