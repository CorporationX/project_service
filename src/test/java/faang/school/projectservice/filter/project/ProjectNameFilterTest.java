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
class ProjectNameFilterTest {

    ProjectNameFilter projectNameFilter = new ProjectNameFilter();
    ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    List<Project> projects;
    String firstName;
    String secondName;

    @BeforeEach
    void setUp() {
        firstName = "first name";
        secondName = "second name";
        Project project1 = Project.builder()
                .name(firstName)
                .build();
        Project project2 = Project.builder()
                .name(firstName)
                .build();
        projects = List.of(project1, project2);
    }

    @Test
    void testIsApplicableWhenNamePatternExist() {
        projectFilterDto.setNamePattern(firstName);
        assertTrue(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableWhenNamePatternNotExist() {
        assertFalse(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyWhenNamePatternExistShouldReturnNotEmptyStream() {
        projectFilterDto.setNamePattern(firstName);
        Stream<Project> result = projectNameFilter.apply(projects.stream(), projectFilterDto);
        assertEquals(projects, result.toList());
    }

    @Test
    void testApplyWhenNamePatternExistAndNoMatchesShouldReturnEmptyStream() {
        projectFilterDto.setNamePattern(secondName);
        List<Project> result = projectNameFilter.apply(projects.stream(), projectFilterDto).toList();
        assertNotEquals(projects, result);
        assertEquals(0, result.size());
    }
}