package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectNameFilterTest {

    @InjectMocks
    private ProjectNameFilter projectNameFilter;

    @Test
    void isApplicable_returns_true_when_namePattern_is_not_null() {
        var filters = new ProjectFilterDto("namePattern", null);

        var result = projectNameFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_returns_false_when_namePattern_is_null() {
        var filters = new ProjectFilterDto(null, null);

        var result = projectNameFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_returns_projects_with_matching_name() {
        var filters = new ProjectFilterDto("name", null);
        var projects = Stream.of(
                Project.builder().name("name one thing you love").status(ProjectStatus.COMPLETED).build(),
                Project.builder().name("nameless").status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().name("crazy").status(ProjectStatus.COMPLETED).build()
        );

        var result = projectNameFilter.apply(projects, filters).toList();

        assertEquals(2, result.size());
    }

}