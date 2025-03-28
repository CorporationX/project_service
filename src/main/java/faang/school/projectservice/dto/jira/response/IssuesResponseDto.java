package faang.school.projectservice.dto.jira.response;

import lombok.Data;

import java.util.List;

@Data
public class IssuesResponseDto {
    private String expand;
    private Integer startAt;
    private Integer maxResults;
    private Integer total;
    private List<IssueResponseDto> issues;
}
