package faang.school.projectservice.dto.jira.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class JiraCreateIssueJSON {
    @NotNull
    Map JSON;
}
