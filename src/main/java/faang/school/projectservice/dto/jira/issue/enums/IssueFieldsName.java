package faang.school.projectservice.dto.jira.issue.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum IssueFieldsName {
    SUMMARY("summary"),
    DESCRIPTION("description"),
    STATUS("status"),
    ASSIGNEE("assignee"),
    ISSUE_TYPE("issuetype");

    private final String fieldName;

    public static List<String> getDefaultFields() {
        return Arrays.stream(IssueFieldsName.values())
                .map(IssueFieldsName::getFieldName)
                .toList();
    }
}
