package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.filter.ProjectStatusFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {
    @InjectMocks
    ProjectStatusFilter projectStatusFilter;

    @Test
    void testIsApplicableReturnsFalse() {
        boolean expected = false;
        ProjectDtoFilter projectDtoFilter = new ProjectDtoFilter();

        boolean actual = projectStatusFilter.isApplicable(projectDtoFilter);

        assertEquals(actual, expected);
    }

    @Test
    void testIsApplicableReturnsTrue() {
        boolean expected = true;
        ProjectDtoFilter projectDtoFilter = getProjectDtoFilter();

        boolean actual = projectStatusFilter.isApplicable(projectDtoFilter);

        assertEquals(actual, expected);
    }

    @Test
    void testApplyThrowNullException() {
        List<Project> projects = null;
        ProjectDtoFilter projectDtoFilter = getProjectDtoFilter();

        assertThrows(NullPointerException.class, () -> projectStatusFilter.apply(projects, projectDtoFilter));
    }

    @Test
    void testApplyThrowUnsupportedOperation() {
        List<Project> projects = getProjects().stream().toList();
        ProjectDtoFilter projectDtoFilter = getProjectDtoFilter();

        assertThrows(UnsupportedOperationException.class, () -> projectStatusFilter.apply(projects, projectDtoFilter));
    }

    @Test
    void testApplyCompleted() {
        List<Project> projects = getProjects();
        ProjectDtoFilter projectDtoFilter = getProjectDtoFilter();

        assertDoesNotThrow(() -> projectStatusFilter.apply(projects, projectDtoFilter));
    }

    private ProjectDtoFilter getProjectDtoFilter() {
        return ProjectDtoFilter.builder().titlePattern("Бизнес").statusPattern(ProjectStatus.IN_PROGRESS).build();
    }

    private List<Project> getProjects() {
        return new ArrayList<>(List.of(
                Project.builder().id(1L).name("Бизнес Фича").description("Проект ДОМ.РФ")
                        .status(ProjectStatus.CREATED).visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().id(2L).name("Аналитика Погоды").description("Проект ДепТранс'а")
                        .status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PUBLIC).build()
        ));
    }
}