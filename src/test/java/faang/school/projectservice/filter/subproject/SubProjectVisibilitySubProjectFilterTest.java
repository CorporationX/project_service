package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubProjectVisibilitySubProjectFilterTest {
    private final SubProjectVisibilitySubProjectFilter subProjectVisibilityFilter = new SubProjectVisibilitySubProjectFilter();

    @Test
    void testIsApplicableFilterNotNullAndPUBLIC() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build();

        boolean result = subProjectVisibilityFilter.isApplicable(filterDto);
        assertTrue(result);
    }

    @Test
    void testIsApplicableFilterNotNullAndPRIVATE() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().visibility(ProjectVisibility.PRIVATE).build();

        boolean result = subProjectVisibilityFilter.isApplicable(filterDto);
        assertFalse(result);
    }

    @Test
    void testIsApplicableFilterNull() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().build();

        boolean result = subProjectVisibilityFilter.isApplicable(filterDto);
        assertFalse(result);
    }


    @Test
    void testApplySuccess() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build();
        List<Project> projectStream = List.of(
                Project.builder().visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().visibility(ProjectVisibility.PRIVATE).build()
        );
        List<Project> projectsFiltered = List.of(Project.builder().visibility(ProjectVisibility.PUBLIC).build());

        Stream<Project> result = subProjectVisibilityFilter.apply(projectStream.stream(), filterDto);
        assertEquals(result.toList(), projectsFiltered);
    }

    @Test
    void testApplyReturnFalseWithFilterNullOrPRIVATE() {
        FilterSubProjectDto filterDtoNull = FilterSubProjectDto.builder().build();
        FilterSubProjectDto filterDtoPRIVATE = FilterSubProjectDto.builder().visibility(ProjectVisibility.PRIVATE).build();

        boolean resultNull = subProjectVisibilityFilter.isApplicable(filterDtoNull);
        boolean resultPRIVATE = subProjectVisibilityFilter.isApplicable(filterDtoPRIVATE);
        assertFalse(resultNull);
        assertFalse(resultPRIVATE);
    }

    @Test
    void testApplyReturnEmptyProjectNotMatch() {
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build();
        List<Project> projectStream = List.of(
                Project.builder().visibility(ProjectVisibility.PRIVATE).build()
        );

        Stream<Project> result = subProjectVisibilityFilter.apply(projectStream.stream(), filterDto);
        assertTrue(result.toList().isEmpty());
    }
}