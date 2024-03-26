package faang.school.projectservice.service.filter;


import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.subproject.filter.SubProjectStatusFilter;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SubProjectStatusFilterTest {
    @InjectMocks
    SubProjectStatusFilter subProjectStatusFilter;

    ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void init() {
        projectFilterDto = CreatingTestData.createProjectFilterDtoForTest();
    }

    @Test
    public void testIsApplicableReturnFalse() {
        ProjectFilterDto newProjectFilterDto = new ProjectFilterDto();

        assertFalse(subProjectStatusFilter.isApplicable(newProjectFilterDto));
    }

    @Test
    public void testIsApplicableReturnTrue() {
        assertTrue(subProjectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void testApplyNullPointerException() {
        Stream<Project> projects = null;

        assertThrows(NullPointerException.class, () -> subProjectStatusFilter.apply(projects, projectFilterDto));
    }

    @Test
    public void testApplyCompleted() {
        Stream<Project> projects = CreatingTestData.createProjectsForTest();

        assertDoesNotThrow(() -> subProjectStatusFilter.apply(projects, projectFilterDto));
    }
}
