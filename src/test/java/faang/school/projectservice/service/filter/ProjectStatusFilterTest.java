package faang.school.projectservice.service.filter;


import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.service.project.filter.ProjectStatusFilter;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {
    @InjectMocks
    ProjectStatusFilter projectStatusFilter;

    ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void init() {
        projectFilterDto = CreatingTestData.createProjectFilterDtoForTest();
    }

    @Test
    public void testIsApplicableReturnFalse() {
        ProjectFilterDto newProjectFilterDto = new ProjectFilterDto();

        assertFalse(projectStatusFilter.isApplicable(newProjectFilterDto));
    }

    @Test
    public void testIsApplicableReturnTrue() {
        assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void testApplyNullPointerException() {
        Stream<Project> projects = null;

        assertThrows(NullPointerException.class, () -> projectStatusFilter.apply(projects, projectFilterDto));
    }

    @Test
    public void testApplyCompleted() {
        Stream<Project> projects = CreatingTestData.createProjectsForTest();

        assertDoesNotThrow(() -> projectStatusFilter.apply(projects, projectFilterDto));
    }
}
