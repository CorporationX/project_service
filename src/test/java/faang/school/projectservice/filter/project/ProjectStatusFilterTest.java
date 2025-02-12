package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.fillters.project.impl.ProjectStatusFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectStatusFilterTest {
    private final ProjectStatusFilter filter = new ProjectStatusFilter();
    private ProjectFilterDto filterDto;

    private Project project1;
    private Project project2;

    Stream<Project> stream;

    @BeforeEach
    public void init() {
        filterDto = new ProjectFilterDto();

        project1 = Project.builder().status(ProjectStatus.CREATED).build();
        project2 = Project.builder().status(ProjectStatus.IN_PROGRESS).build();

        stream = Stream.of(project1, project2);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setStatusPattern(ProjectStatus.CREATED);

        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(project1, actual.get(0));
    }

    @Test
    public void testApplyWithNonMatchingStatus() {
        filterDto.setStatusPattern(ProjectStatus.COMPLETED);

        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(0, actual.size());
    }

    @Test
    public void testApplyWithStatusPatternNull() {
        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithDifferentStatus() {
        filterDto.setStatusPattern(ProjectStatus.IN_PROGRESS);

        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(project2, actual.get(0));
    }
}
