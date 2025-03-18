package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {

    private final ProjectStatusFilter filter = new ProjectStatusFilter();
    private final ProjectStatus firstStatus = ProjectStatus.CREATED;
    private final ProjectStatus secondStatus = ProjectStatus.IN_PROGRESS;

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(new ProjectFilterDto(null, firstStatus));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveApplicableNullStatus() {
        boolean isApplicable = filter.isApplicable(new ProjectFilterDto(null, null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplyStatusExist() {
        Stream<Project> projectStream = Stream.of(createProject(firstStatus), createProject(secondStatus));

        List<Project> projects = filter.apply(projectStream, new ProjectFilterDto(null, firstStatus))
                .toList();
        assertEquals(1, projects.size());
        assertEquals(firstStatus, projects.get(0).getStatus());
    }

    @Test
    public void testPositiveApplyStatusNotExist() {
        Stream<Project> projectStream = Stream.of(createProject(firstStatus), createProject(firstStatus));

        List<Project> projects = filter.apply(projectStream, new ProjectFilterDto(null, secondStatus))
                .toList();
        assertEquals(0, projects.size());
    }

    private Project createProject(ProjectStatus status) {
        return Project.builder()
                .status(status)
                .build();
    }

}
