package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNameFilterTest {

    private final ProjectNameFilter nameFilter = new ProjectNameFilter();

    @Test
    void testIsApplicableFalse() {
        boolean result = nameFilter.isApplicable(ProjectFilterDto.builder().build());
        assertFalse(result);
    }

    @Test
    void testIsApplicableTrue() {
        boolean result = nameFilter.isApplicable(ProjectFilterDto.builder().name("Project").build());
        assertTrue(result);
    }

    @Test
    void testIsApplicableEmptyName() {
        boolean result = nameFilter.isApplicable(ProjectFilterDto.builder().name("  ").build());
        assertFalse(result);
    }

    @Test
    void testApplyMatchesExist() {
        ProjectFilterDto dto = ProjectFilterDto.builder().name("Bakery").build();
        Stream<Project> projects = Stream.of(
                Project.builder().id(1L).name("BakerY").build(),
                Project.builder().id(2L).name("Laundry").build(),
                Project.builder().id(3L).name("bakEry").build()
        );
        List<Project> projectList = nameFilter.apply(projects, dto).toList();

        assertEquals(2, projectList.size());
        assertEquals(dto.getName().toLowerCase(), projectList.get(0).getName().toLowerCase());
        assertEquals(dto.getName().toLowerCase(), projectList.get(1).getName().toLowerCase());
    }

    @Test
    void testApplyNoMatches(){
        ProjectFilterDto dto = ProjectFilterDto.builder().name("Bakery").build();
        Stream<Project> projects = Stream.of(
                Project.builder().id(1L).name("forge").build(),
                Project.builder().id(2L).name("Laundry").build(),
                Project.builder().id(3L).name("theaTer").build()
        );
        List<Project> projectList = nameFilter.apply(projects, dto).toList();

        assertEquals(0, projectList.size());
    }
}