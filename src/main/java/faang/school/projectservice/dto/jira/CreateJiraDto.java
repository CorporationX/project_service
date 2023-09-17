package faang.school.projectservice.dto.jira;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJiraDto {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Project key cannot be blank")
    private String projectKey;
    @NotBlank(message = "Project url cannot be blank")
    private String projectUrl;
    @NotNull(message = "Project key cannot be null")
    private Long projectId;
    @NotBlank(message = "Token cannot be blank")
    private String token;
}
