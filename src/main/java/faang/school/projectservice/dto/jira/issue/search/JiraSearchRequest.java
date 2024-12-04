package faang.school.projectservice.dto.jira.issue.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JiraSearchRequest {

    @JsonProperty("jql")
    private String jql;

    @JsonProperty("maxResults")
    private Integer maxResults;

    @JsonProperty("fields")
    private List<String> fields;

    public static JiraSearchRequest createDefault(String jql) {
        return JiraSearchRequest.builder()
                .jql(jql)
                .maxResults(100)
                .fields(Arrays.asList("summary", "description", "status", "assignee"))
                .build();
    }
}
