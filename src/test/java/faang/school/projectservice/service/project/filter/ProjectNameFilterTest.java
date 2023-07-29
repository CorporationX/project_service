package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ProjectNameFilterTest {
    private final ProjectNameFilter nameFilter = new ProjectNameFilter();

    private List<Project> projects;

    @BeforeEach
    public void initProjects() {
        projects = List.of(
                Project.builder()
                        .name("Unicorn project")
                        .build(),
                Project.builder()
                        .name("Kraken project")
                        .build(),
                Project.builder()
                        .name("Unity project")
                        .build()
        );
    }

    @Test
    public void shouldReturnTrueIfPatternIsSpecified() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .namePattern("Uni")
                .build();

        boolean isApplicable = nameFilter.isApplicable(filters);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void shouldReturnFalseIfPatternIsNotSpecified() {
        ProjectFilterDto filters = new ProjectFilterDto();

        boolean isApplicable = nameFilter.isApplicable(filters);

        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void shouldReturnSortedProjectsList() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .namePattern("Uni")
                .build();
        List<Project> desiredProject = List.of(
                Project.builder()
                        .name("Unicorn project")
                        .build(),
                Project.builder()
                        .name("Unity project")
                        .build()
        );

        Stream<Project> receivedProjects = nameFilter.apply(projects.stream(), filters);

        Assertions.assertEquals(desiredProject, receivedProjects.toList());
    }
}
