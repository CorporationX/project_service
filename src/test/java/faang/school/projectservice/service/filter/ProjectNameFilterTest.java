package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectNameFilterTest {
    private final ProjectNameFilter projectNameFilter = new ProjectNameFilter();
    private List<Project> projects;

    @BeforeEach
    public void setUp() {
        projects = List.of(
                Project.builder()
                        .name("Project1")
                        .build(),
                Project.builder()
                        .name("Project12")
                        .build(),
                Project.builder()
                        .name("Project3")
                        .build()
        );
    }

    @Test
    void shouldReturnTrueIfFilterIsSpecified() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .namePattern("Project1")
                .build();
        assertTrue(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void shouldReturnFalseIfFilterIsSpecified() {
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        assertFalse(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void shouldReturnFilteredProjectList() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .namePattern("Project1")
                .build();
        List<Project> filteredProjects = projectNameFilter.apply(projects.stream(), projectFilterDto).toList();
        assertEquals(2, filteredProjects.size());
        assertEquals("Project1", filteredProjects.get(0).getName());
        assertEquals("Project12", filteredProjects.get(1).getName());
    }
}