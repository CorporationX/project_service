package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
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
public class ProjectNameFilterTest {

    @InjectMocks
    ProjectNameFilter projectNameFilter;

    private final ProjectFilterDto projectFilterDto = new ProjectFilterDto();

    private static final List<Project> projects = new ArrayList<>();
    private static final Project projectEmptyName = new Project();
    private static final Project projectValidName = new Project();

    @BeforeAll
    static void setup() {
        projectEmptyName.setName("");
        projectValidName.setName("name");
        projects.add(projectEmptyName);
        projects.add(projectValidName);
    }

    @Test
    void isApplicable_notApplicable_returnsFalse() {
        projectFilterDto.setNamePattern("name");
        boolean result = projectNameFilter.isApplicable(projectFilterDto);
        assertTrue(result);
    }

    @Test
    void isApplicable_applicable_returnsTrue() {
        projectFilterDto.setNamePattern(null);
        boolean result = projectNameFilter.isApplicable(projectFilterDto);
        assertFalse(result);
    }

    @Test
    void apply_namePatternMatchingOneProject_returnsOneMatchingProject() {
        projectFilterDto.setNamePattern("name");
        Stream<Project> result = projectNameFilter.apply(projects, projectFilterDto);
        assertEquals(1, result.toList().size());
    }
}