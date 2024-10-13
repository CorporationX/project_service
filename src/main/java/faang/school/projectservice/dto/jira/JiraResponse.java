package faang.school.projectservice.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JiraResponse {

    private String expand;

    private Integer startAt;

    private Integer maxResults;

    private Integer total;

    private List<IssueDto> issues;
}