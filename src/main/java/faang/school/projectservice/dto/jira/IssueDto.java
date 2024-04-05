package faang.school.projectservice.dto.jira;

import com.atlassian.jira.rest.client.api.domain.IssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueDto {
    private String projectKey;
    private IssueType issueType;
    private String summary;
    private String description;
}
