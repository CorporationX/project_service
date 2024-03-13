package faang.school.projectservice.model.jira;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueAssignee {
    private String accountId;
    private String displayName;
}
