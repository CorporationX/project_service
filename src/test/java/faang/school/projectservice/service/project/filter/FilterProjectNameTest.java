package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.filter.FilterProjectName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterProjectNameTest {
    private FilterProjectName filterProjectName = new FilterProjectName();
    private ProjectFilterDto projectFilterDto;

    @Test
    public void isApplication_WhenNameIsNotNull_ShouldReturnTrue() {
        projectFilterDto = new ProjectFilterDto("test", null);
        boolean result = filterProjectName.isApplication(projectFilterDto);

        assertTrue(result);
    }

    @Test
    public void isApplication_WhenNameIsNull_ShouldReturnFalse() {
        projectFilterDto = new ProjectFilterDto(null, null);
        boolean result = filterProjectName.isApplication(projectFilterDto);

        assertFalse(result);
    }

    @Test
    void apply_WhenProjectsContainName_ShouldReturnFilteredStream() {
        Project project1 = Project.builder().name("test Project 1").build();
        Project project2 = Project.builder().name("Project test 2").build();
        Project project3 = Project.builder().name("Project 3").build();
        Stream<Project> projects = Stream.of(project1, project2, project3);
        ProjectFilterDto filterDto = new ProjectFilterDto("test", null);

        Stream<Project> result = filterProjectName.apply(projects, filterDto);

        List<Project> resultList = result.toList();
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(project1));
        assertTrue(resultList.contains(project2));
        assertFalse(resultList.contains(project3));
    }

    @Test
    void apply_WhenNoProjectsContainName_ShouldReturnEmptyStream() {
        Project project1 = Project.builder().name("apple-project").build();
        Project project2 = Project.builder().name("banana-project").build();

        Stream<Project> projects = Stream.of(project1, project2);
        ProjectFilterDto filterDto = new ProjectFilterDto("orange",null);

        Stream<Project> result = filterProjectName.apply(projects, filterDto);

        List<Project> resultList = result.toList();
        assertTrue(resultList.isEmpty());
    }
}