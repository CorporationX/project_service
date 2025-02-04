package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectNameFilterTest {
    private ProjectFilterDto filterDto;
    private ProjectNameFilter filter;
    private Project project1;
    private Project project2;
    private Project project3;

    @BeforeEach
    void setUp() {
        filterDto = new ProjectFilterDto();
        filter = new ProjectNameFilter();
        project1 = new Project();
        project2 = new Project();
        project3 = new Project();
    }

    @Test
    void isApplicableTest_shouldReturnTrue_whenNamePatternIsNotNull() {
        filterDto.setNamePattern("Project A");

        boolean result = filter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_shouldReturnFalse_whenNamePatternIsNull() {
        boolean result = filter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    void applyTest_shouldFilterProjectsByNamePattern() {
        filterDto.setNamePattern("Project A");
        project1.setName("Project A");
        project2.setName("Project B");
        project3.setName("Project A");
        List<Project> input = List.of(project1, project2, project3);
        List<Project> expected = List.of(project1, project3);

        List<Project> result = filter.apply(input.stream(), filterDto).toList();

        assertEquals(expected, result);
    }

    @Test
    void applyTest_shouldReturnEmpty_whenNoProjectsMatchNamePattern() {
        filterDto.setNamePattern("Project C");
        project1.setName("Project A");
        project2.setName("Project B");
        List<Project> input = List.of(project1, project2);

        List<Project> result = filter.apply(input.stream(), filterDto).toList();

        assertEquals(List.of(), result);
    }
}