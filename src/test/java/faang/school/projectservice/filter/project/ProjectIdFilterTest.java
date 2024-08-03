package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectIdFilterTest {

    ProjectIdFilter projectIdFilter = new ProjectIdFilter();
    ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    List<Project> projects;
    Long projectId;
    Long anotherProjectId;

    @BeforeEach
    void setUp() {
        projectId = 1L;
        anotherProjectId = 2L;
        Project project1 = Project.builder()
                .id(projectId)
                .build();
        Project project2 = Project.builder()
                .id(projectId)
                .build();
        projects = List.of(project1, project2);
    }

    @Test
    void testIsApplicableWhenNamePatternExist() {
        projectFilterDto.setIdPattern(projectId);
        assertTrue(projectIdFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableWhenNamePatternNotExist() {
        assertFalse(projectIdFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyWhenNamePatternExistShouldReturnNotEmptyStream() {
        projectFilterDto.setIdPattern(projectId);
        Stream<Project> result = projectIdFilter.apply(projects.stream(), projectFilterDto);
        assertEquals(projects, result.toList());
    }

    @Test
    void testApplyWhenNamePatternExistAndNoMatchesShouldReturnEmptyStream() {
        projectFilterDto.setIdPattern(anotherProjectId);
        List<Project> result = projectIdFilter.apply(projects.stream(), projectFilterDto).toList();
        assertNotEquals(projects, result);
        assertEquals(0, result.size());
    }
}