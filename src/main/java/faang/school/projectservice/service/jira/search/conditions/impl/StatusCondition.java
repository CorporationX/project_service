package faang.school.projectservice.service.jira.search.conditions.impl;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.Condition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatusCondition implements Condition {

    private static final String STATUS_FIELD_NAME = "status";
    private static final String STATUS_FIELD_CONDITION = "status=%s";

    private final String status;

    @Override
    public void apply(JiraSearchRequestBuilder builder) {
        if (status != null && status.isBlank()) {
            builder.appendCondition(
                    STATUS_FIELD_NAME,
                    String.format(STATUS_FIELD_CONDITION, status));
        }
    }
}
