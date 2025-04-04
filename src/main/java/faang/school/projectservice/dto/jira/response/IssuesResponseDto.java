package faang.school.projectservice.dto.jira.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuesResponseDto {
    private String expand;
    private Integer startAt;
    private Integer maxResults;
    private Integer total;
    private List<IssueResponseDto> issues;
}
