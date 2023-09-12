package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueLinkCreationDto {

    private Type type;

    private InwardIssue inwardIssue;

    private OutwardIssue outwardIssue;

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Type {
        private String name;
    }

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InwardIssue {
        private String key;
    }

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OutwardIssue {
        private String key;
    }
}
