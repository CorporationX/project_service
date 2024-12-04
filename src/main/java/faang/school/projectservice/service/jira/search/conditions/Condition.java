package faang.school.projectservice.service.jira.search.conditions;

import faang.school.projectservice.service.jira.builders.JiraSearchRequestBuilder;

public interface Condition {

    void apply(JiraSearchRequestBuilder builder);
}
