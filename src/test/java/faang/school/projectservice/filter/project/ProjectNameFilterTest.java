package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectNameFilterTest {
    private final ProjectFilter projectNameFilter = new ProjectNameFilter();
    private ProjectFilterDto nullProjectFilterDto;
    private ProjectFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        nullProjectFilterDto = new ProjectFilterDto(null, null);
        filterDto = new ProjectFilterDto("project", null);
    }

    @Test
    @DisplayName("Проверка метода isApplicable с пустым полем namePattern")
    public void testIsApplicableWithNullNamePattern() {
        boolean result = projectNameFilter.isApplicable(nullProjectFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверка метода isApplicable с непустым полем namePattern")
    public void testIsApplicableWithNotNullNamePattern() {
        boolean result = projectNameFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    @DisplayName("Проверка фильтрации списка проектов")
    public void testSuccessApplyNameFilter() {
        List<Project> projects = prepareProjects();
        List<Project> result = projectNameFilter.apply(projects.stream(), filterDto).toList();
        assertEquals(3, result.size());

        ProjectFilterDto projectFilterDto = new ProjectFilterDto("fsfsf", null);
        result = projectNameFilter.apply(projects.stream(), projectFilterDto).toList();
        assertEquals(0, result.size());
    }

    private List<Project> prepareProjects() {
        long idCounter = 0;
        Project project1 = Project.builder()
                .id(++idCounter)
                .name("project")
                .build();
        Project project2 = Project.builder()
                .id(++idCounter)
                .name("123123")
                .build();
        Project project3 = Project.builder()
                .id(++idCounter)
                .name("Project 3")
                .build();
        Project project4 = Project.builder()
                .id(++idCounter)
                .name("PROJECT")
                .build();
        return List.of(project1, project2, project3, project4);
    }
}
