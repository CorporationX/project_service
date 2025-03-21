package faang.school.projectservice.dto.jira.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueLinkDto {
    private IssueLinkType issueLinkType;
    private OutwardIssue outwardIssue;
    private InwardIssue inwardIssue;
}
