package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static faang.school.projectservice.util.TestProject.PROJECTS_LIST;
import static faang.school.projectservice.util.TestProject.PROJECT_FILTERED;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ProjectNameFilterTest {

    private final ProjectNameFilter projectNameFilter = new ProjectNameFilter();

    @Test
    public void testShouldReturnTrueIfFilterSpecified() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .name("Project first")
                .build();
        boolean isApplicable = projectNameFilter.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    public void testShouldReturnFalseIfFilterNotSpecified() {
        ProjectFilterDto filters = new ProjectFilterDto();
        boolean isApplicable  = projectNameFilter.isApplicable(filters);
        assertFalse(isApplicable);
    }

    @Test
    public void testShouldReturnFilteredProjectList() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .name("Project first")
                .build();
        Stream<Project> filteredProject = projectNameFilter.apply(PROJECTS_LIST.stream(), filters);
        assertEquals(PROJECT_FILTERED, filteredProject.toList());
    }
}