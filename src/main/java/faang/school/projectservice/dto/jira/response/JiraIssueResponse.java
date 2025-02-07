package faang.school.projectservice.dto.jira.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JiraIssueResponse {
    private String id;
    private String key;
    private String self;
}