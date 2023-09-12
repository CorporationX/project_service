package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueStatusUpdateDto {

    @NotNull
    @Pattern(regexp = "^(?i)(to do|in progress|done)$",
            message = "Status must be one of following: to do, in progress or done")
    private String status;
}

