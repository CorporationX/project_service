package faang.school.projectservice.service.jira.search.conditions.impl;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.Condition;
import faang.school.projectservice.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MultipleStatusCondition implements Condition {

    private static final String STATUS_IN_FIELD_NAME = "status";
    private static final String STATUS_IN_FIELD_CONDITION = "status in (%s)";

    private final List<String> statuses;

    @Override
    public void apply(JiraSearchRequestBuilder builder) {
        if (CollectionUtils.isNotEmpty(statuses)) {
            builder.appendCondition(
                    STATUS_IN_FIELD_NAME,
                    String.format(
                            STATUS_IN_FIELD_CONDITION,
                            String.join(",", statuses)
                    ));
        }
    }
}
