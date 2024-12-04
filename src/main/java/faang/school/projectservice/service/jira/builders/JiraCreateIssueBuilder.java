package faang.school.projectservice.service.jira.builders;

import faang.school.projectservice.dto.jira.adf.ADFDocument;
import faang.school.projectservice.dto.jira.issue.enums.IssueType;
import faang.school.projectservice.dto.jira.issue.nested.IssueTypeRef;
import faang.school.projectservice.dto.jira.issue.nested.Project;
import faang.school.projectservice.dto.jira.issue.create.CreateFields;
import faang.school.projectservice.dto.jira.issue.create.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.nested.Assignee;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class JiraCreateIssueBuilder {

    private String projectKey;
    private String summary;
    private String description;
    private IssueType issueType;
    private String assigneeAccountId;
    private List<String> labels;

    public JiraCreateIssueRequest build() {
        CreateFields createFields = new CreateFields();

        Optional.ofNullable(projectKey)
                .map(Project::new)
                .ifPresent(createFields::setProject);

        Optional.ofNullable(summary).
                ifPresent(createFields::setSummary);

        Optional.ofNullable(description)
                .map(this::createDescription)
                .ifPresent(createFields::setDescription);

        Optional.ofNullable(issueType)
                .map(issueType -> new IssueTypeRef(issueType.getId()))
                .ifPresent(createFields::setIssuetype);

        Optional.ofNullable(assigneeAccountId)
                .map(Assignee::new)
                .ifPresent(createFields::setAssignee);

        Optional.ofNullable(labels)
                .filter(labels -> !labels.isEmpty())
                .ifPresent(createFields::setLabels);

        return new JiraCreateIssueRequest(createFields);
    }

    private ADFDocument createDescription(String text) {
        return new ADFBuilder().addText(text).build();
    }

    public JiraCreateIssueBuilder setProjectKey(String projectKey) {
        this.projectKey = projectKey;
        return this;
    }

    public JiraCreateIssueBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public JiraCreateIssueBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public JiraCreateIssueBuilder setIssueType(IssueType issueType) {
        this.issueType = issueType;
        return this;
    }

    public JiraCreateIssueBuilder setAssigneeAccountId(String assigneeAccountId) {
        this.assigneeAccountId = assigneeAccountId;
        return this;
    }

    public JiraCreateIssueBuilder setLabels(List<String> labels) {
        this.labels = labels;
        return this;
    }
}
