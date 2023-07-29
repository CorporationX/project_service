package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class ProjectStatusFilterTest {
    private final ProjectStatusFilter statusFilter = new ProjectStatusFilter();

    private List<Project> projects;

    @BeforeEach
    public void initProjects() {
        projects = List.of(
                Project.builder()
                        .status(ProjectStatus.CREATED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.CANCELLED)
                        .build()
        );
    }

    @Test
    public void shouldReturnTrueIfPatternIsSpecified() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .statuses(List.of(ProjectStatus.CREATED))
                .build();

        boolean isApplicable = statusFilter.isApplicable(filters);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void shouldReturnFalseIfPatternIsNotSpecified() {
        ProjectFilterDto filters = new ProjectFilterDto();

        boolean isApplicable = statusFilter.isApplicable(filters);

        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void shouldReturnSortedProjectsList() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .statuses(List.of(ProjectStatus.CREATED, ProjectStatus.COMPLETED))
                .build();
        List<Project> desiredProjects = List.of(
                Project.builder()
                        .status(ProjectStatus.CREATED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.COMPLETED)
                        .build()
        );

        Stream<Project> receivedProjects = statusFilter.apply(projects.stream(), filters);

        Assertions.assertEquals(desiredProjects, receivedProjects.toList());
    }
}
