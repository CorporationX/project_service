package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

public class ProjectNameFilterTest {
    private final ProjectFilterDto EMPTY_PROJECT_FILTER_DTO = new ProjectFilterDto();
    private final ProjectFilterDto PROJECT_FILTER_DTO = ProjectFilterDto.builder().name("name").build();
    private final ProjectNameFilter projectNameFilter = new ProjectNameFilter();
    private Stream<Project> projects;

    @BeforeEach
    void init() {
        projects = Stream.of(
                Project.builder().name("some name").build(),
                Project.builder().name("some other name").build(),
                Project.builder().name("new project").build()
        );
    }

    @Test
    public void applicableFalseTest() {
        Assertions.assertFalse(projectNameFilter.isApplicable(EMPTY_PROJECT_FILTER_DTO));
    }

    @Test
    public void applicableTrueTest() {
        Assertions.assertTrue(projectNameFilter.isApplicable(PROJECT_FILTER_DTO));
    }

    @Test
    public void applyTest() {
        List<Project> expectedList = List.of(
                Project.builder().name("some name").build(),
                Project.builder().name("some other name").build());

        Stream<Project> apply = projectNameFilter.apply(projects, PROJECT_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }

}
