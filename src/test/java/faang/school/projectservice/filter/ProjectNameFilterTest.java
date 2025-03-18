package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectNameFilterTest {

    private final ProjectNameFilter filter = new ProjectNameFilter();
    private final String firstName = "name";
    private final String secondName = "no name";

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(new ProjectFilterDto(firstName, null));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveApplicableNullName() {
        boolean isApplicable = filter.isApplicable(new ProjectFilterDto(null, null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplicableEmptyName() {
        boolean isApplicable = filter.isApplicable(new ProjectFilterDto("", null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplicableBlankName() {
        boolean isApplicable = filter.isApplicable(new ProjectFilterDto("       ", null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplyNameExist() {

        Stream<Project> projectStream = Stream.of(createProject(firstName), createProject(secondName));

        List<Project> projects = filter.apply(projectStream, new ProjectFilterDto(firstName, null))
                .toList();
        assertEquals(1, projects.size());
        assertEquals(firstName, projects.get(0).getName());
    }

    @Test
    public void testPositiveApplyNameNotExist() {
        Stream<Project> projectStream = Stream.of(createProject(firstName), createProject(secondName));

        List<Project> projects = filter.apply(projectStream, new ProjectFilterDto("another name", null))
                .toList();
        assertEquals(0, projects.size());
    }

    @Test
    public void testPositiveApplyIgnoreCase() {
        Stream<Project> projectStream = Stream.of(createProject(firstName), createProject("NaMe"));

        List<Project> projects = filter.apply(projectStream, new ProjectFilterDto(firstName, null))
                .toList();
        assertEquals(2, projects.size());
        assertEquals(firstName, projects.get(0).getName().toLowerCase());
        assertEquals(firstName, projects.get(1).getName().toLowerCase());
    }

    private Project createProject(String name) {
        return Project.builder()
                .name(name)
                .build();
    }

}
