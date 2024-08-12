package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectNameFilterTest {

    private List<Project> projectList;
    private ProjectFilterDto projectFilterDto;
    private ProjectNameFilter projectNameFilter;

    @BeforeEach
    public void setUp() {
        projectFilterDto = ProjectFilterDto.builder()
                .name("name").build();
        projectNameFilter = new ProjectNameFilter();
        projectList = List.of(
                Project.builder()
                        .name("name").build(),
                Project.builder()
                        .name("not")
                        .build()
        );
    }

    @Test
    @DisplayName("testing isApplicable with appropriate value")
    public void testIsApplicableWithAppropriateValue() {
        assertTrue(projectNameFilter.isApplicable(projectFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable with non appropriate value")
    public void testIsApplicableWithNonAppropriateValue() {
        assertFalse(projectNameFilter.isApplicable(new ProjectFilterDto()));
    }

    @Test
    @DisplayName("testing filter")
    public void testFilter() {
        List<Project> matchedProjects = projectNameFilter.filter(projectList.stream(), projectFilterDto).toList();
        assertEquals(1, matchedProjects.size());
        assertEquals(projectList.get(0), matchedProjects.get(0));
    }
}