package faang.school.projectservice.service.jira.builders;

import faang.school.projectservice.dto.jira.issue.enums.IssueFieldsName;
import faang.school.projectservice.dto.jira.issue.search.JiraSearchRequest;
import faang.school.projectservice.service.jira.search.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public class JiraSearchRequestBuilder {

    private static final String AND_CONDITION = " AND ";

    private boolean hasCondition = false;
    private final List<Condition> conditions = new ArrayList<>();

    private Integer maxResults = 100;
    private final StringBuilder query = new StringBuilder();
    private final List<String> fields = new ArrayList<>();

    public JiraSearchRequest build() {
        conditions.forEach(c -> c.apply(this));

        return JiraSearchRequest.builder()
                .jql(query.toString())
                .maxResults(maxResults)
                .fields(fields.stream().distinct().toList())
                .build();
    }

    public JiraSearchRequestBuilder withDefaultFields() {
        fields.addAll(IssueFieldsName.getDefaultFields());
        return this;
    }

    public JiraSearchRequestBuilder maxResults(Integer maxResults) {
        if (maxResults != null && maxResults > 0) {
            this.maxResults = maxResults;
        }
        return this;
    }

    public JiraSearchRequestBuilder addCondition(Condition condition) {
        conditions.add(condition);
        return this;
    }

    public void appendCondition(String field, String condition) {
        if (hasCondition) {
            query.append(AND_CONDITION);
        }
        query.append(condition);
        fields.add(field);
        hasCondition = true;
    }
}
