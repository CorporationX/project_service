package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {

    @InjectMocks
    ProjectStatusFilter projectStatusFilter;

    private final ProjectFilterDto projectFilterDto = new ProjectFilterDto();

    private static final List<Project> projects = new ArrayList<>();
    private static final Project projectStatusCreated = new Project();
    private static final Project projectStatusCancelled = new Project();

    @BeforeAll
    static void setup() {
        projectStatusCreated.setStatus(ProjectStatus.CREATED);
        projectStatusCancelled.setStatus(ProjectStatus.CANCELLED);
        projects.add(projectStatusCreated);
        projects.add(projectStatusCancelled);
    }

    @Test
    void isApplicable_notApplicable_returnsFalse() {
        projectFilterDto.setStatus(ProjectStatus.CREATED);
        boolean result = projectStatusFilter.isApplicable(projectFilterDto);
        assertTrue(result);
    }

    @Test
    void isApplicable_applicable_returnsTrue() {
        projectFilterDto.setStatus(null);
        boolean result = projectStatusFilter.isApplicable(projectFilterDto);
        assertFalse(result);
    }

    @Test
    void apply_statusMatchesOneProject_returnsOneMatchingProject() {
        projectFilterDto.setStatus(ProjectStatus.CREATED);
        Stream<Project> result = projectStatusFilter.apply(projects, projectFilterDto);
        assertEquals(1, result.toList().size());
    }
}