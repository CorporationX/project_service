package faang.school.projectservice.dto.jira.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.dto.jira.filter.AssigneeDto;
import faang.school.projectservice.dto.parent.ParentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueUpdateDto {
    private Fields fields;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fields {
        private String description;
        private String duedate;
        private AssigneeDto assignee;
        private ParentDto parentDto;
        private List<IssueLinkDto> issueLinks;
    }
}
