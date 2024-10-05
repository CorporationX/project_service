package faang.school.projectservice.filter;

import faang.school.projectservice.dto.jira.Assignee;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IssueAssigneeFilterTest {

    private final IssueAssigneeFilter issueAssigneeFilter = new IssueAssigneeFilter();

    @Test
    void isApplicable_FilterIsNull() {
        boolean isApplicable = issueAssigneeFilter.isApplicable(null);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicable_AssigneeIsNull() {
        IssueFilterDto filterDto = new IssueFilterDto();

        boolean isApplicable = issueAssigneeFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicable_WhenOk() {
        Assignee assignee = new Assignee();
        IssueFilterDto filterDto = IssueFilterDto.builder()
                .assignee(assignee)
                .build();

        boolean isApplicable = issueAssigneeFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    void createJql() {
        String accountId = "accountId";
        Assignee assignee = new Assignee();
        assignee.setAccountId(accountId);
        IssueFilterDto filterDto = IssueFilterDto.builder()
                .assignee(assignee)
                .build();
        String correctJql = "transition = '%s'".formatted(accountId);

        String result = issueAssigneeFilter.createJql(filterDto);

        assertEquals(correctJql, result);
    }
}