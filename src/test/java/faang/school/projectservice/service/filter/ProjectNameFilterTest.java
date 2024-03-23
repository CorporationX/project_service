package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.service.project.filter.ProjectNameFilter;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProjectNameFilterTest {
    @InjectMocks
    ProjectNameFilter projectNameFilter;
    ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void init() {
        projectFilterDto = CreatingTestData.createProjectFilterDtoForTest();
    }

    @Test
    public void testIsApplicableReturnFalse() {
        ProjectFilterDto newProjectFilterDto = new ProjectFilterDto();

        assertFalse(projectNameFilter.isApplicable(newProjectFilterDto));
    }

    @Test
    public void testIsApplicableReturnTrue() {
        assertTrue(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    public void testApplyNullPointerException() {
        Stream<Project> projects = null;

        assertThrows(NullPointerException.class, () -> projectNameFilter.apply(projects, projectFilterDto));
    }

    @Test
    public void testApplyCompleted() {
        Stream<Project> projects = CreatingTestData.createProjectsForTest();

        assertDoesNotThrow(() -> projectNameFilter.apply(projects, projectFilterDto));
    }
}