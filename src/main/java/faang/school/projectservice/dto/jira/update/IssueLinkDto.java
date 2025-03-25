package faang.school.projectservice.dto.jira.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class IssueLinkDto {
    private IssueLinkType type;
    private OutwardIssue outwardIssue;
    private InwardIssue inwardIssue;
}
