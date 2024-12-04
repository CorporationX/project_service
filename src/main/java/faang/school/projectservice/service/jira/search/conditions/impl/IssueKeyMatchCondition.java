package faang.school.projectservice.service.jira.search.conditions.impl;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;
import faang.school.projectservice.service.jira.search.conditions.Condition;
import faang.school.projectservice.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IssueKeyMatchCondition implements Condition {

    private static final String ISSUE_KEY_FIELD_NAME = "issuekey";
    public static final String ISSUE_KEY_FIELD_CONDITION = "issuekey=%s";

    private final String issueKey;


    @Override
    public void apply(JiraSearchRequestBuilder builder) {
        if (StringUtils.isNotBlank(issueKey)) {
            builder.appendCondition(
                    ISSUE_KEY_FIELD_NAME,
                    String.format(ISSUE_KEY_FIELD_CONDITION, issueKey)
            );
        }
    }
}
