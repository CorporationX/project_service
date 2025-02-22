package faang.school.projectservice.dto.jira.request.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.request.IssueTypeRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueFieldsCreateRequestDto {

    private ProjectCreateDto project;

    private String summary;

    private String description;

    @JsonProperty("issuetype")
    private IssueTypeRequestDto issueType;

    @Data
    static class ProjectCreateDto {

        private String key;

    }

}