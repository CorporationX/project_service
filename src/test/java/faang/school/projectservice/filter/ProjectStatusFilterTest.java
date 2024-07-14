package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class ProjectStatusFilterTest {
    private final ProjectFilterDto EMPTY_PROJECT_FILTER_DTO = new ProjectFilterDto();
    private final ProjectFilterDto PROJECT_FILTER_DTO = ProjectFilterDto.builder().projectStatus(ProjectStatus.CREATED).build();
    private final ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
    private Stream<Project> projects;

    @BeforeEach
    void init(){
        projects = Stream.of(
                Project.builder().status(ProjectStatus.CREATED).build(),
                Project.builder().status(ProjectStatus.COMPLETED).build(),
                Project.builder().status(ProjectStatus.IN_PROGRESS).build()
        );
    }

    @Test
    public void applicableFalseTest() {
        Assertions.assertFalse(projectStatusFilter.isApplicable(EMPTY_PROJECT_FILTER_DTO));
    }

    @Test
    public void applicableTrueTest() {
        Assertions.assertTrue(projectStatusFilter.isApplicable(PROJECT_FILTER_DTO));
    }

    @Test
    public void applyTest() {
        List<Project> expectedList = List.of(
                Project.builder().status(ProjectStatus.CREATED).build());

        Stream<Project> apply = projectStatusFilter.apply(projects, PROJECT_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
