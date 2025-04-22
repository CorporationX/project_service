package faang.school.projectservice.dto.jira.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueLinkDto {
    private IssueLinkType type;
    private OutwardIssue outwardIssue;
    private InwardIssue inwardIssue;
}
