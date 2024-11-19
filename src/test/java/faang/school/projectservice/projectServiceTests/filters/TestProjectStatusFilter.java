package faang.school.projectservice.projectServiceTests.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filters.projectFilters.ProjectFilterByStatus;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectStatus.CREATED;

@ExtendWith(MockitoExtension.class)
public class TestProjectStatusFilter {
    private final ProjectFilterByStatus filterByStatus = new ProjectFilterByStatus();

    private List<Project> projectStream;

    @BeforeEach
    void init() {
        projectStream = List.of(
                Project.builder()
                        .status(CREATED)
                        .build(),
                Project.builder()
                        .status(COMPLETED)
                        .build(),
                Project.builder()
                        .status(CANCELLED)
                        .build()
        );
    }

    @Test
    public void mustReturnTrueIfFilterIsApplicable() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .status(CREATED)
                .build();

        boolean isApplicable = filterByStatus.isApplicable(filters);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void mustReturnFalseIfFilterNotApplicable() {
        ProjectFilterDto filters = ProjectFilterDto.builder().build();

        boolean isApplicable = filterByStatus.isApplicable(filters);
        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void mustReturnFilteredProjectListWithNewStatus() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .status(CREATED)
                .build();

        List<Project> desireProjects = List.of(
                Project.builder()
                        .status(CREATED)
                        .build()
        );

        Stream<Project> receivedProjects = filterByStatus.apply(projectStream.stream(), filters);
        Assertions.assertEquals(desireProjects, receivedProjects.toList());
    }
}
