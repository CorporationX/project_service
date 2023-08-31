package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectStatusFilterTest {
    private final ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
    private List<Project> projects;

    @BeforeEach
    public void setUp() {
        projects = List.of(
                Project.builder()
                        .name("Project1")
                        .status(ProjectStatus.valueOf("IN_PROGRESS"))
                        .build(),
                Project.builder()
                        .name("Project12")
                        .status(ProjectStatus.valueOf("CREATED"))
                        .build(),
                Project.builder()
                        .name("Project3")
                        .status(ProjectStatus.valueOf("IN_PROGRESS"))
                        .build()
        );
    }

    @Test
    void shouldReturnTrueIfFilterIsSpecified() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .statusPattern("IN_PROGRESS")
                .build();
        assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void shouldReturnFalseIfFilterIsSpecified() {
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        assertFalse(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void shouldReturnFiltredProjectList() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .statusPattern("IN_PROGRESS")
                .build();
        List<Project> filteredProjects = projectStatusFilter.apply(projects.stream(), projectFilterDto).toList();
        assertEquals(2, filteredProjects.size());
        assertEquals("Project1", filteredProjects.get(0).getName());
        assertEquals("Project3", filteredProjects.get(1).getName());
    }
}