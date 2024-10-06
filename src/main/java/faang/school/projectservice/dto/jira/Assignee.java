package faang.school.projectservice.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignee {
    private String accountId;
    private String displayName;
}
