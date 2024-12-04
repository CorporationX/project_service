package faang.school.projectservice.service.jira.search.conditions.impl;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.Condition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectCondition implements Condition {

    private static final String PROJECT_FIELD_NAME = "project";
    private static final String PROJECT_FIELD_CONDITION = "project=%s";

    private final String projectKey;

    @Override
    public void apply(JiraSearchRequestBuilder builder) {
        if (projectKey != null && !projectKey.isBlank()) {
            builder.appendCondition(
                    PROJECT_FIELD_NAME,
                    String.format(PROJECT_FIELD_CONDITION, projectKey));
        }
    }
}
