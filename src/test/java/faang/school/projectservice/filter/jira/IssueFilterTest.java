package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.IssueFilterDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IssueFilterTest {

    private List<IssueFilter> filters;
    private IssueFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filters = List.of(
                new IssueAssigneeFilter(),
                new IssueStatusFilter(),
                new IssueProjectFilter()
        );
    }

    @Test
    void isApplicable_shouldReturnAssigneeFilter() {
        filterDto = IssueFilterDto.builder()
                .assignee("user")
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof IssueAssigneeFilter),
                        Assertions::fail
                );
    }

    @Test
    void isApplicable_shouldReturnStatusFilter() {
        filterDto = IssueFilterDto.builder()
                .status("status")
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof IssueStatusFilter),
                        Assertions::fail
                );
    }

    @Test
    void isApplicable_shouldReturnProjectFilter() {
        filterDto = IssueFilterDto.builder()
                .project("project")
                .build();

        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof IssueProjectFilter),
                        Assertions::fail
                );
    }

    @Test
    void apply_shouldReturnFilterBuilderWithAllFields() {
        filterDto = IssueFilterDto.builder()
                .assignee("user")
                .status("status")
                .project("project")
                .build();

        StringBuilder filterBuilderActual = new StringBuilder();

        filters.forEach(filter -> filter.apply(filterBuilderActual, filterDto));

        String filterExpected = "assignee=\"user\"&status=\"status\"&project=\"project\"";

        assertEquals(filterExpected, filterBuilderActual.toString());
    }
}