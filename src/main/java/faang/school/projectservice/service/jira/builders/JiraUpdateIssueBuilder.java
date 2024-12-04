package faang.school.projectservice.service.jira.builders;

import faang.school.projectservice.dto.jira.issue.enums.IssueType;
import faang.school.projectservice.dto.jira.issue.nested.IssueTypeRef;
import faang.school.projectservice.dto.jira.issue.update.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.adf.ADFDocument;
import faang.school.projectservice.dto.jira.issue.nested.Assignee;
import faang.school.projectservice.dto.jira.issue.link.IssueLink;
import faang.school.projectservice.dto.jira.issue.link.IssueLinkDetails;
import faang.school.projectservice.dto.jira.issue.link.LinkType;
import faang.school.projectservice.dto.jira.issue.link.OutwardIssue;
import faang.school.projectservice.dto.jira.issue.nested.Parent;
import faang.school.projectservice.dto.jira.issue.update.UpdateData;
import faang.school.projectservice.dto.jira.issue.update.UpdateFields;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class JiraUpdateIssueBuilder {

    private String summary;
    private String description;
    private String assigneeAccountId;
    private String parentIssueKey;
    private List<String> linkedIssueKeys;
    private IssueType issueType;

    public JiraUpdateIssueRequest build() {
        UpdateFields updateFields = new UpdateFields();
        UpdateData updateData = new UpdateData();

        Optional.ofNullable(summary)
                .ifPresent(updateFields::setSummary);

        Optional.ofNullable(description)
                .map(this::createDescription)
                .ifPresent(updateFields::setDescription);

        Optional.ofNullable(issueType)
                .map(issueType -> new IssueTypeRef(issueType.getId()))
                .ifPresent(updateFields::setIssuetype);

        Optional.ofNullable(assigneeAccountId)
                .map(Assignee::new)
                .ifPresent(updateFields::setAssignee);

        Optional.ofNullable(parentIssueKey)
                .map(Parent::new)
                .ifPresent(updateFields::setParent);

        Optional.ofNullable(linkedIssueKeys)
                .filter(keys -> !keys.isEmpty())
                .ifPresent(keys -> {
                    List<IssueLink> linkUpdates = keys.stream()
                            .map(this::createIssueLink)
                            .toList();
                    updateData.setIssuelinks(linkUpdates);
                });
        return new JiraUpdateIssueRequest(updateFields, updateData);
    }

    private ADFDocument createDescription(String text) {
        return new ADFBuilder()
                .addText(text)
                .build();
    }

    private IssueLink createIssueLink(String linkedKey) {
        IssueLinkDetails issueLinkDetails = IssueLinkDetails.builder()
                .type(new LinkType("Relates"))
                .outwardIssue(new OutwardIssue(linkedKey))
                .build();

        return new IssueLink(issueLinkDetails);
    }

    public JiraUpdateIssueBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public JiraUpdateIssueBuilder setIssueType(IssueType issueType) {
        this.issueType = issueType;
        return this;
    }

    public JiraUpdateIssueBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public JiraUpdateIssueBuilder setAssigneeAccountId(String assigneeAccountId) {
        this.assigneeAccountId = assigneeAccountId;
        return this;
    }

    public JiraUpdateIssueBuilder setParentIssueKey(String parentIssueKey) {
        this.parentIssueKey = parentIssueKey;
        return this;
    }

    public JiraUpdateIssueBuilder setLinkedIssueKeys(List<String> linkedIssueKeys) {
        this.linkedIssueKeys = linkedIssueKeys;
        return this;
    }
}
