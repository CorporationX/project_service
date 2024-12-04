package faang.school.projectservice.dto.jira.issue.nested;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueTypeRef {

    @JsonProperty("id")
    private String id;
}
