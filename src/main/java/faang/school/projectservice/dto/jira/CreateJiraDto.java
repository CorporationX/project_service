package faang.school.projectservice.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJiraDto {
    private String username;
    private String projectKey;
    private String projectUrl;
    private Long projectId;
    private String token;
}
