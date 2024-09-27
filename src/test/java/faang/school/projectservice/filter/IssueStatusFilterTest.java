package faang.school.projectservice.filter;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IssueStatusFilterTest {

    private final IssueStatusFilter issueStatusFilter = new IssueStatusFilter();

    @Test
    void isApplicable_FilterIsNull() {
        boolean isApplicable = issueStatusFilter.isApplicable(null);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicable_StatusIsNull() {
        IssueFilterDto filterDto = new IssueFilterDto();

        boolean isApplicable = issueStatusFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    void isApplicable_WhenOk() {
        IssueDto.Status status = new IssueDto.Status();
        IssueFilterDto filterDto = IssueFilterDto.builder()
                .status(status)
                .build();

        boolean isApplicable = issueStatusFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    void createJql() {
        String name = "name";
        IssueDto.Status status = new IssueDto.Status();
        status.setName(name);
        IssueFilterDto filterDto = IssueFilterDto.builder()
                .status(status)
                .build();
        String correctJql = "status = '%s'".formatted(name);

        String result = issueStatusFilter.createJql(filterDto);

        assertEquals(correctJql, result);
    }
}