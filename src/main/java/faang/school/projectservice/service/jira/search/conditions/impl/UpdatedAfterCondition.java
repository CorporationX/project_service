package faang.school.projectservice.service.jira.search.conditions.impl;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.Condition;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class UpdatedAfterCondition implements Condition {

    private static final String UPDATED_FIELD_NAME = "updated";
    private static final String UPDATED_FIELD_CONDITION = "updated >= '%s'";

    private final LocalDateTime updatedAfter;

    @Override
    public void apply(JiraSearchRequestBuilder builder) {
        if (updatedAfter != null) {
            builder.appendCondition(
                    UPDATED_FIELD_NAME,
                    String.format(
                            UPDATED_FIELD_CONDITION,
                            updatedAfter.format(DateTimeFormatter.ISO_DATE)
                    ));
        }
    }
}
