package projectservice.jira;

import faang.school.projectservice.dto.jira.filter.AssigneeDto;
import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueStatusRequestDto;
import faang.school.projectservice.filter.jira.AssigneeFilter;
import faang.school.projectservice.filter.jira.IssueStatusFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class IssueFilterTest {
    private final AssigneeFilter assigneeFilter = new AssigneeFilter();
    private final IssueStatusFilter issueStatusFilter = new IssueStatusFilter();
    private IssueFilterDto issueFilterDto;

    @BeforeEach
    public void startUp() {
        issueFilterDto = new IssueFilterDto(new AssigneeDto("name"), new IssueStatusRequestDto("status"));
    }

    @Test
    public void testAssigneeFilter_notApplicable() {
        assertFalse(assigneeFilter.isApplicable(null));

        issueFilterDto.getAssignee().setName(null);
        assertFalse(assigneeFilter.isApplicable(issueFilterDto));

        issueFilterDto.setAssignee(null);
        assertFalse(assigneeFilter.isApplicable(issueFilterDto));
    }

    @Test
    public void testAssigneeFilter_createJql() {
        assertEquals("transition = " + issueFilterDto.getAssignee().getName(),
                assigneeFilter.createJql(issueFilterDto));
    }

    @Test
    public void testIssueStatusFilter_notApplicable() {
        assertFalse(issueStatusFilter.isApplicable(null));

        issueFilterDto.getStatus().setName(null);
        assertFalse(issueStatusFilter.isApplicable(issueFilterDto));

        issueFilterDto.setStatus(null);
        assertFalse(issueStatusFilter.isApplicable(issueFilterDto));
    }

    @Test
    public void testIssueStatusFilter_createJql() {
        assertEquals("status = \"" + issueFilterDto.getStatus().getName() + "\"",
                issueStatusFilter.createJql(issueFilterDto));
    }
}
