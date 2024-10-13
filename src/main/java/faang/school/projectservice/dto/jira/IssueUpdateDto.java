package faang.school.projectservice.dto.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueUpdateDto {
    private Fields fields;
    private Transition transition;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fields {

        private String description;
        private String duedate;
        private Assignee assignee;
        private Parent parent;
        private List<IssueLink> issueLinks;
    }

    @Data
    public static class IssueLink {
        private IssueLinkType type;
        private OutwardIssue outwardIssue;
        private InwardIssue inwardIssue;

        @Data
        public static class IssueLinkType {
            private String id;
            private String name;
            private String inward;
            private String outward;
        }

        @Data
        public static class InwardIssue {
            private String id;
            private String key;
        }

        @Data
        public static class OutwardIssue {
            private String id;
            private String key;
        }
    }

    @Data
    public static class Transition {
        private String id;
    }
}