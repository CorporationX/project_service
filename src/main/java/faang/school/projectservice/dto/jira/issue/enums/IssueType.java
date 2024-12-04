package faang.school.projectservice.dto.jira.issue.enums;

import faang.school.projectservice.config.jira.JiraIssueStatusConfig;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
public enum IssueType {
    TASK,
    SUBTASK,
    BUG;

    private String id;

    @Component
    @RequiredArgsConstructor
    public static class Initializer {

        private final JiraIssueStatusConfig issueStatusConfig;

        @PostConstruct
        public void init() {
            Map<String, String> ids = issueStatusConfig.getIds();

            for (IssueType issueType : IssueType.values()) {
                issueType.id = ids.get(issueType.name());
            }
        }
    }
}
