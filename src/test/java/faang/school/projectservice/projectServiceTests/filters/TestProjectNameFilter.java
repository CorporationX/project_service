package faang.school.projectservice.projectServiceTests.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filters.projectFilters.ProjectFilterByName;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class TestProjectNameFilter {

    private final ProjectFilterByName nameFilter = new ProjectFilterByName();

    private List<Project> projectStream;

    @BeforeEach
    public void initFilter() {
        projectStream = List.of(
                Project.builder()
                        .name("New name")
                        .build(),
                Project.builder()
                        .name("Second Project")
                        .build(),
                Project.builder()
                        .name("Last Project")
                        .build()
        );
    }

    @Test
    public void mustReturnTrueIsFilterApplicable() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .name("Second Project")
                .build();

        boolean isApplicable = nameFilter.isApplicable(filterDto);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void mustReturnFalseIfFilterNotApplicable() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .build();

        boolean isApplicable = nameFilter.isApplicable(filterDto);

        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void mustReturnFilteredProjectList() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .name("Second Project")
                .build();

        List<Project> desireProjects = List.of(
                Project.builder()
                        .name("Second Project")
                        .build()
        );
        Stream<Project> receivedProjects = nameFilter.apply(projectStream.stream(), filterDto);
        Assertions.assertEquals(desireProjects, receivedProjects.toList());
    }
}
