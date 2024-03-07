package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
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
class ProjectStatusFilterTest {
    @InjectMocks
    private ProjectStatusFilter projectStatusFilter;
    private List<Project> projects;

    @BeforeEach
    public void init() {
        projects = List.of(
                Project.builder()
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.CANCELLED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.CREATED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.ON_HOLD)
                        .build()
        );
    }

    @Test
    public void testReturnTrueIfFilterIsSpecified() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        boolean isApplicable = projectStatusFilter.isApplicable(filterDto);

        assertTrue(isApplicable);
    }

    @Test
    public void testReturnFalseIfFilterIsSpecified() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        boolean isApplicable = projectStatusFilter.isApplicable(filterDto);

        assertFalse(isApplicable);
    }

    @Test
    public void testReturnFilteredProjectList() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        List<Project> expectedProjects = Collections.singletonList(
                Project.builder()
                        .status(ProjectStatus.COMPLETED)
                        .build()
        );
        Stream<Project> tempProjects = projectStatusFilter.apply(projects.stream(), filterDto);
        List<Project> actualProjects = tempProjects.toList();

        assertTrue(expectedProjects.size() == actualProjects.size()
                && expectedProjects.containsAll(actualProjects));
    }
}