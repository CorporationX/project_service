package faang.school.projectservice.integration.jira.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthStatusResponse {
    private Long userId;
    private boolean connected;
}
