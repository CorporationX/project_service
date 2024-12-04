package faang.school.projectservice.service.jira.search.conditions.impl;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.Condition;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class CreatedAfterCondition implements Condition {

    private static final String CREATED_AFTER_FIELD_NAME = "created";
    private static final String CREATED_AFTER_FIELD_CONDITION = "created >= '%s'";

    private final LocalDateTime dateAfter;

    @Override
    public void apply(JiraSearchRequestBuilder builder) {
        if (dateAfter != null) {
            builder.appendCondition(
                    CREATED_AFTER_FIELD_NAME,
                    String.format(
                            CREATED_AFTER_FIELD_CONDITION,
                            dateAfter.format(DateTimeFormatter.ISO_DATE)
                    ));
        }
    }
}
