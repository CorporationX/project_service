package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubProjectStatusSubProjectFilterTest {
    private final SubProjectStatusSubProjectFilter subProjectStatusFilter = new SubProjectStatusSubProjectFilter();

    @Test
    void testIsApplicableFilterNotNull() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().status(ProjectStatus.COMPLETED).build();

        boolean result = subProjectStatusFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    void testIsApplicableFilterNull() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().build();

        boolean result = subProjectStatusFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplySuccess() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        List<Project> projectStream = List.of(
                Project.builder().status(ProjectStatus.COMPLETED).build(),
                Project.builder().status(ProjectStatus.IN_PROGRESS).build()
        );
        List<Project> projectsFiltered = List.of(Project.builder().status(ProjectStatus.COMPLETED).build());

        Stream<Project> result = subProjectStatusFilter.apply(projectStream.stream(), filterDto);
        assertEquals(result.toList(), projectsFiltered);
    }

    @Test
    void testApplyReturnFalseWithFilterNull() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().build();

        boolean result = subProjectStatusFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testApplyReturnEmptyProjectNotMatch() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        List<Project> projectStream = List.of(
                Project.builder().status(ProjectStatus.IN_PROGRESS).build()
        );

        Stream<Project> result = subProjectStatusFilter.apply(projectStream.stream(), filterDto);
        assertTrue(result.toList().isEmpty());
    }
}