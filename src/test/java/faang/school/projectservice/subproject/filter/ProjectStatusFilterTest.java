package faang.school.projectservice.subproject.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.subproject.ProjectStatusFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectStatusFilterTest {

    private List<Project> projectList;
    private ProjectFilterDto projectFilterDto;
    private ProjectStatusFilter projectStatusFilter;

    @BeforeEach
    public void setUp() {
        projectFilterDto = ProjectFilterDto.builder()
                .projectStatus(ProjectStatus.CREATED).build();
        projectStatusFilter = new ProjectStatusFilter();
        projectList = List.of(
                Project.builder()
                        .status(ProjectStatus.CREATED).build(),
                Project.builder()
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()
        );
    }

    @Test
    @DisplayName("testing isApplicable with appropriate value")
    public void testIsApplicableWithAppropriateValue() {
        assertTrue(projectStatusFilter.isApplicable(projectFilterDto));
    }

    @Test
    @DisplayName("testing isApplicable with non appropriate value")
    public void testIsApplicableWithNonAppropriateValue() {
        assertFalse(projectStatusFilter.isApplicable(new ProjectFilterDto(null,null)));
    }

    @Test
    @DisplayName("testing filter")
    public void testFilter() {
        List<Project> matchedProjects = projectStatusFilter.filter(projectList.stream(), projectFilterDto).toList();
        assertEquals(1, matchedProjects.size());
        assertEquals(projectList.get(0), matchedProjects.get(0));
    }
}