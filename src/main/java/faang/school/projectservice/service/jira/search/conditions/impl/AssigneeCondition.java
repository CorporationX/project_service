package faang.school.projectservice.service.jira.search.conditions.impl;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.Condition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssigneeCondition implements Condition {

    private static final String ASSIGNEE_FIELD_NAME = "assignee";
    private static final String ASSIGNEE_FIELD_CONDITION = "assignee='%s'";

    private final String assignee;

    @Override
    public void apply(JiraSearchRequestBuilder builder) {
        if (assignee != null && !assignee.isEmpty()) {
            builder.appendCondition(
                    ASSIGNEE_FIELD_NAME,
                    String.format(
                            ASSIGNEE_FIELD_CONDITION,
                            assignee)
            );
        }
    }
}
