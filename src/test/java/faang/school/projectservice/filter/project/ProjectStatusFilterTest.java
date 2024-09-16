package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectStatusFilterTest {
    private final ProjectFilter projectStatusFilter = new ProjectStatusFilter();
    private ProjectFilterDto nullProjectFilterDto;
    private ProjectFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        nullProjectFilterDto = new ProjectFilterDto(null, null);
        filterDto = new ProjectFilterDto(null, ProjectStatus.CREATED);
    }

    @Test
    @DisplayName("Проверка метода isApplicable с пустым полем projectStatus")
    public void testIsApplicableWithNullNamePattern() {
        boolean result = projectStatusFilter.isApplicable(nullProjectFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверка метода isApplicable с непустым полем projectStatus")
    public void testIsApplicableWithNotNullNamePattern() {
        boolean result = projectStatusFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    @DisplayName("Проверка фильтрации списка проектов")
    public void testSuccessApplyNameFilter() {
        List<Project> projects = prepareProjects();
        List<Project> result = projectStatusFilter.apply(projects.stream(), filterDto).toList();
        assertEquals(2, result.size());

        ProjectFilterDto projectFilterDto = new ProjectFilterDto(null, ProjectStatus.ON_HOLD);
        result = projectStatusFilter.apply(projects.stream(), projectFilterDto).toList();
        assertEquals(1, result.size());
    }

    private List<Project> prepareProjects() {
        long idCounter = 0;
        Project project1 = Project.builder()
                .id(++idCounter)
                .status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .id(++idCounter)
                .status(ProjectStatus.CREATED)
                .build();
        Project project3 = Project.builder()
                .id(++idCounter)
                .status(ProjectStatus.CANCELLED)
                .build();
        Project project4 = Project.builder()
                .id(++idCounter)
                .status(ProjectStatus.ON_HOLD)
                .build();
        return List.of(project1, project2, project3, project4);
    }
}
