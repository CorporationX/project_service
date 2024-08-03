package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectStatusFilterTest {

    ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
    ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    List<Project> projects;

    @BeforeEach
    void setUp() {
        Project project1 = Project.builder()
                .status(ProjectStatus.CREATED)
                .build();
        Project project2 = Project.builder()
                .status(ProjectStatus.CREATED)
                .build();
        projects = List.of(project1, project2);
    }

    @Test
    void testIsApplicableWhenVisibilityPatternExist() {
        projectFilterDto.setStatusPattern(ProjectStatus.CREATED);
        assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableWhenVisibilityPatternNotExist() {
        assertFalse(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyWhenVisibilityPatternExistShouldReturnNotEmptyStream() {
        projectFilterDto.setStatusPattern(ProjectStatus.CREATED);
        Stream<Project> result = projectStatusFilter.apply(projects.stream(), projectFilterDto);
        assertEquals(projects, result.toList());
    }

    @Test
    void testApplyWhenVisibilityPatternExistAndNoMatchesShouldReturnEmptyStream() {
        projectFilterDto.setStatusPattern(ProjectStatus.ON_HOLD);
        List<Project> result = projectStatusFilter.apply(projects.stream(), projectFilterDto).toList();
        assertNotEquals(projects, result);
        assertEquals(0, result.size());
    }
}