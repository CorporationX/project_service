package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDescriptionFilterTest {

    ProjectDescriptionFilter projectDescriptionFilter = new ProjectDescriptionFilter();
    ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    List<Project> projects;
    String firstDescription;
    String secondDescription;

    @BeforeEach
    void setUp() {
        firstDescription = "first description";
        secondDescription = "second description";
        Project project1 = Project.builder()
                .description(firstDescription)
                .build();
        Project project2 = Project.builder()
                .description(firstDescription)
                .build();
        projects = List.of(project1, project2);
    }

    @Test
    void testIsApplicableWhenNamePatternExist() {
        projectFilterDto.setDescriptionPattern(firstDescription);
        assertTrue(projectDescriptionFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testIsApplicableWhenNamePatternNotExist() {
        assertFalse(projectDescriptionFilter.isApplicable(projectFilterDto));
    }

    @Test
    void testApplyWhenNamePatternExistShouldReturnNotEmptyStream() {
        projectFilterDto.setDescriptionPattern(firstDescription);
        Stream<Project> result = projectDescriptionFilter.apply(projects.stream(), projectFilterDto);
        assertEquals(projects, result.toList());
    }

    @Test
    void testApplyWhenNamePatternExistAndNoMatchesShouldReturnEmptyStream() {
        projectFilterDto.setDescriptionPattern(secondDescription);
        List<Project> result = projectDescriptionFilter.apply(projects.stream(), projectFilterDto).toList();
        assertNotEquals(projects, result);
        assertEquals(0, result.size());
    }
}