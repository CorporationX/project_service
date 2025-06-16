package faang.school.projectservice.dto.jira.issue.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JiraCreateIssueResponseDto {

        private String id;
        private String key;
        private String self;
        private Transition transition;

        @Data
        public static class Transition {
            private int status;
            private ErrorCollection errorCollection;
        }

        @Data
        public static class ErrorCollection {
            private List<String> errorMessages;
            private Map<String, String> errors;
        }

}
