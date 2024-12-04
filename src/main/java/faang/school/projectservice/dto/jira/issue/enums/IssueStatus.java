package faang.school.projectservice.dto.jira.issue.enums;

import faang.school.projectservice.config.jira.JiraTransitionConfig;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
public enum IssueStatus {
    TODO,
    IN_PROGRESS,
    DONE;

    private String id;

    @Component
    @RequiredArgsConstructor
    public static class Initializer {

        private final JiraTransitionConfig taskStatusConfig;

        @PostConstruct
        public void init() {
            Map<String, String> ids = taskStatusConfig.getIds();

            for (IssueStatus issueStatus : IssueStatus.values()) {
                issueStatus.id = ids.get(issueStatus.name());
            }
        }
    }
}
