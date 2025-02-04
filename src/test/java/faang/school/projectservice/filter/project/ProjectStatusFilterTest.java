package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectStatusFilterTest {
    private ProjectFilterDto filterDto;
    private ProjectStatusFilter filter;
    private Project project1;
    private Project project2;
    private Project project3;

    @BeforeEach
    void setUp() {
        filterDto = new ProjectFilterDto();
        filter = new ProjectStatusFilter();
        project1 = new Project();
        project2 = new Project();
        project3 = new Project();
    }

    @Test
    void isApplicableTest_shouldReturnTrue_whenProjectStatusIsNotNull() {
        filterDto.setProjectStatus(ProjectStatus.IN_PROGRESS);

        boolean result = filter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_shouldReturnFalse_whenProjectStatusIsNull() {
        boolean result = filter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void applyTest_shouldFilterProjectsByStatus() {
        filterDto.setProjectStatus(ProjectStatus.CREATED);
        project1.setStatus(ProjectStatus.CREATED);
        project2.setStatus(ProjectStatus.COMPLETED);
        project3.setStatus(ProjectStatus.CREATED);
        List<Project> input = List.of(project1, project2, project3);
        List<Project> expected = List.of(project1, project3);

        List<Project> result = filter.apply(input.stream(), filterDto).toList();

        assertEquals(expected, result);
    }

    @Test
    void applyTest_shouldReturnEmpty_whenNoProjectsMatchStatus() {
        filterDto.setProjectStatus(ProjectStatus.ON_HOLD);
        project1.setStatus(ProjectStatus.CREATED);
        project2.setStatus(ProjectStatus.CANCELLED);
        List<Project> input = List.of(project1, project2);

        List<Project> result = filter.apply(input.stream(), filterDto).toList();

        assertEquals(List.of(), result);
    }
}