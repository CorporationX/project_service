package faang.school.projectservice.dto.jira.issue.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.dto.jira.issue.Fields;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JiraUpdateIssueRequest {
        private Map<String, List<Operation>> update;
        private Fields fields;
        private HistoryMetadata historyMetadata;
        private List<Property> properties;

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Operation {
            private String add;
            private String set;
            private String remove;
            private String edit;
            // Добавьте другие операции при необходимости
        }

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class HistoryMetadata {
            private String type;
            private String description;
            private String descriptionKey;
            private String activityDescription;
            private String activityDescriptionKey;
            private String emailDescription;
            private String emailDescriptionKey;
            private Actor actor;
            private Generator generator;
            private Cause cause;
            private Map<String, Object> extraData;

            @Data
            public static class Actor {
                private String id;
                private String displayName;
                private String type;
                private String avatarUrl;
                private String url;
            }

            @Data
            public static class Generator {
                private String id;
                private String displayName;
                private String type;
                private String avatarUrl;
                private String url;
            }

            @Data
            public static class Cause {
                private String id;
                private String displayName;
                private String type;
                private String avatarUrl;
                private String url;
            }
        }

        @Data
        public static class Property {
            private String key;
            private Object value;
        }
    }
