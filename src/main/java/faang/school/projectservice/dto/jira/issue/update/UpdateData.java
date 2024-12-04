package faang.school.projectservice.dto.jira.issue.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.issue.link.IssueLink;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateData {

    @JsonProperty("issuelinks")
    private List<IssueLink> issuelinks;
}
