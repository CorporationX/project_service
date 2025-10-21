package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.filter.FilterProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterProjectStatusTest {
    private FilterProjectStatus filterProjectStatus = new FilterProjectStatus();
    private ProjectFilterDto projectFilterDto;

    @Test
    public void isApplication_WhenStatusIsNotNull_ShouldReturnTrue() {
        projectFilterDto = new ProjectFilterDto(null, ProjectStatus.IN_PROGRESS);
        boolean result = filterProjectStatus.isApplication(projectFilterDto);

        assertTrue(result);
    }

    @Test
    public void isApplication_WhenStatusIsNull_ShouldReturnFalse() {
        projectFilterDto = new ProjectFilterDto(null, null);
        boolean result = filterProjectStatus.isApplication(projectFilterDto);

        assertFalse(result);
    }

    @Test
    void apply_WhenProjectsContainStatus_ShouldReturnFilteredStream() {
        Project project1 = Project.builder().status(ProjectStatus.COMPLETED).build();
        Project project2 = Project.builder().status(ProjectStatus.CREATED).build();
        Project project3 = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        Project project4 = Project.builder().status(ProjectStatus.ON_HOLD).build();
        Stream<Project> projects = Stream.of(project1, project2, project3, project4);
        ProjectFilterDto filterDto = new ProjectFilterDto(null, ProjectStatus.IN_PROGRESS);

        Stream<Project> result = filterProjectStatus.apply(projects, filterDto);

        List<Project> resultList = result.toList();
        assertEquals(1, resultList.size());
        assertFalse(resultList.contains(project1));
        assertFalse(resultList.contains(project2));
        assertTrue(resultList.contains(project3));
        assertFalse(resultList.contains(project4));
    }

    @Test
    void apply_WhenNoProjectsContainStatus_ShouldReturnEmptyStream() {
        Project project1 = Project.builder().status(ProjectStatus.COMPLETED).build();
        Project project2 = Project.builder().status(ProjectStatus.CREATED).build();

        Stream<Project> projects = Stream.of(project1, project2);
        ProjectFilterDto filterDto = new ProjectFilterDto(null,ProjectStatus.IN_PROGRESS);

        Stream<Project> result = filterProjectStatus.apply(projects, filterDto);

        List<Project> resultList = result.toList();
        assertTrue(resultList.isEmpty());
    }
}
