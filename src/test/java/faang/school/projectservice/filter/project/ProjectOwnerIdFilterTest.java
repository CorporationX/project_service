package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectOwnerIdFilterTest {

    ProjectOwnerIdFilter projectOwnerIdFilter = new ProjectOwnerIdFilter();
    ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    List<Project> projects;
    Long ownerId;
    Long someUserId;

    @BeforeEach
    void setUp() {
        ownerId = 1L;
        someUserId = 2L;
        Project project1 = Project.builder()
                .ownerId(ownerId)
                .build();
        Project project2 = Project.builder()
                .ownerId(ownerId)
                .build();
        projects = List.of(project1, project2);
    }

    @Test
    void testIsApplicableWhenOwnerIdPatternExist() {
        projectFilterDto.setOwnerIdPattern(ownerId);
        assertTrue(projectOwnerIdFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableWhenOwnerIdPatternNotExist() {
        assertFalse(projectOwnerIdFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyWhenOwnerIdPatternExistShouldReturnNotEmptyStream() {
        projectFilterDto.setOwnerIdPattern(ownerId);
        Stream<Project> result = projectOwnerIdFilter.apply(projects.stream(), projectFilterDto);
        assertEquals(projects, result.toList());
    }

    @Test
    void testApplyWhenOwnerIdPatternExistAndNoMatchesShouldReturnEmptyStream() {
        projectFilterDto.setOwnerIdPattern(someUserId);
        List<Project> result = projectOwnerIdFilter.apply(projects.stream(), projectFilterDto).toList();
        assertNotEquals(projects, result);
        assertEquals(0, result.size());
    }
}