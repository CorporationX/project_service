package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectStatusFilterTest {

    private final ProjectStatusFilter statusFilter = new ProjectStatusFilter();

    @Test
    void testIsApplicableFalse() {
        boolean result = statusFilter.isApplicable(ProjectDto.builder().build());
        assertFalse(result);
    }

    @Test
    void testIsApplicableTrue() {
        boolean result = statusFilter.isApplicable(ProjectDto.builder().status(ProjectStatus.CREATED).build());
        assertTrue(result);
    }

    @Test
    void testApplyMatchesExist() {
        ProjectDto dto = ProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        Stream<Project> projects = Stream.of(
                Project.builder().id(1L).status(ProjectStatus.CREATED).build(),
                Project.builder().id(2L).status(ProjectStatus.COMPLETED).build(),
                Project.builder().id(3L).status(ProjectStatus.COMPLETED).build()
        );
        List<Project> projectList = statusFilter.apply(projects, dto).toList();

        assertEquals(2, projectList.size());
        assertEquals(dto.getStatus(), projectList.get(0).getStatus());
        assertEquals(dto.getStatus(), projectList.get(1).getStatus());
    }

    @Test
    void testApplyNoMatches(){
        ProjectDto dto = ProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        Stream<Project> projects = Stream.of(
                Project.builder().id(1L).status(ProjectStatus.CREATED).build(),
                Project.builder().id(2L).status(ProjectStatus.ON_HOLD).build(),
                Project.builder().id(3L).status(ProjectStatus.CANCELLED).build()
        );
        List<Project> projectList = statusFilter.apply(projects, dto).toList();

        assertEquals(0, projectList.size());
    }
}