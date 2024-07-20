package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectStatusFilterTest {

    @InjectMocks
    private ProjectStatusFilter projectStatusFilter;

    @Test
    void isApplicableShouldReturnTrueWhenStatusPatternIsNotNull() {
        var filters = new ProjectFilterDto(null, "statusPattern");

        var result = projectStatusFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicableShouldReturnFalseWhenStatusPatternIsNull() {
        var filters = new ProjectFilterDto(null, null);

        var result = projectStatusFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void applyShouldReturnProjectsWithMatchingStatus() {
        var filters = new ProjectFilterDto(null, "completed");
        var projects = Stream.of(
                Project.builder().name("name").status(ProjectStatus.COMPLETED).build(),
                Project.builder().name("name").status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().name("name").status(ProjectStatus.COMPLETED).build()
        );

        var result = projectStatusFilter.apply(projects, filters).toList();

        assertEquals(2, result.size());
    }
}