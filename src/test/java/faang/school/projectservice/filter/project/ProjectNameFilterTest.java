package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProjectNameFilterTest {
    @InjectMocks
    private ProjectNameFilter projectNameFilter;
    List<Project> projects;

    @BeforeEach
    public void init() {
        projects = List.of(
                Project.builder()
                        .name("Alpha")
                        .build(),
                Project.builder()
                        .name("Beta")
                        .build(),
                Project.builder()
                        .name("Gamma")
                        .build(),
                Project.builder()
                        .name("Delta")
                        .build()
        );
    }

    @Test
    void testReturnIsTrueIfFilterIsSpecified() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .name("Gamma")
                .build();
        boolean isApplicable = projectNameFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFalseIfFilterIsSpecified() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        boolean isApplicable = projectNameFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnFilteredProjectList() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .name("Delta")
                .build();
        List<Project> expectedProjects = Collections.singletonList(
                Project.builder()
                        .name("Delta")
                        .build()
        );
        Stream<Project> tempProjects = projectNameFilter.apply(projects.stream(), filterDto);
        List<Project> actualProjects = tempProjects.toList();

        assertTrue(expectedProjects.size() == actualProjects.size()
                && expectedProjects.containsAll(actualProjects));
    }
}