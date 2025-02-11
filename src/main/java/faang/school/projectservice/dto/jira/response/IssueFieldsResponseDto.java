package faang.school.projectservice.dto.jira.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.dto.jira.AssigneeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueFieldsResponseDto {

    @JsonProperty("issuetype")
    private IssueTypeResponseDto issueType;

    private ProjectResponseDto project;

    private StatusDto status;

    private String summary;

    private String description;

    private AssigneeDto assignee;

    @Data
    static class StatusDto {
        private String name;
    }

    @Data
    static class ProjectResponseDto {

        private String id;

        private String key;

        private String name;
    }

    @Data
    static class IssueTypeResponseDto {

        private String id;

        private String description;

        private String name;
    }

}