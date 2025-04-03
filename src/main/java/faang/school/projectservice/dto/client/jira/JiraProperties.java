package faang.school.projectservice.dto.client.jira;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JiraProperties {

    @NotBlank(message = "Your Jira url is empty")
    private final String baseUrl;

    @NotBlank(message = "Your email is empty")
    private final String email;

    @NotBlank(message = "Api token value is empty")
    private String apiToken;
}
