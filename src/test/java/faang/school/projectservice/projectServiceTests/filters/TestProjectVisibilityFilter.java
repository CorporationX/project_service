package faang.school.projectservice.projectServiceTests.filters;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filters.projectFilters.FilterByProjectVisibility;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import faang.school.projectservice.validator.projectservice.ProjectParticipantValidatorByVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestProjectVisibilityFilter {
    @Mock
    private ProjectParticipantValidatorByVisibility visibilityValidator;

    @Mock
    private UserContext userContext;

    private FilterByProjectVisibility filterByVisibility;

    private List<Project> projectStream;

    @BeforeEach
    void setUp() {
        filterByVisibility = new FilterByProjectVisibility(visibilityValidator, userContext);

        projectStream = List.of(
                Project.builder().visibility(ProjectVisibility.PUBLIC).build(),
                Project.builder().visibility(ProjectVisibility.PRIVATE)
                        .build(),
                Project.builder().visibility(ProjectVisibility.PRIVATE)
                        .build()
        );
    }

    @Test
    void shouldReturnTrueIfFilterIsApplicable() {
        ProjectFilterDto filters = ProjectFilterDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        boolean isApplicable = filterByVisibility.isApplicable(filters);
        assertTrue(isApplicable);
    }

    @Test
    void shouldReturnFalseIfFilterIsNotApplicable() {
        ProjectFilterDto filters = ProjectFilterDto.builder().build();

        boolean isApplicable = filterByVisibility.isApplicable(filters);
        assertFalse(isApplicable);
    }


    @Test
    void shouldReturnFilteredProjectsWithPublicVisibility() {
        Long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);

        ProjectFilterDto filters = ProjectFilterDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        List<Project> expectedProjects = List.of(
                Project.builder()
                        .visibility(ProjectVisibility.PUBLIC)
                        .build()
        );

        Stream<Project> filteredProjects = filterByVisibility.apply(projectStream.stream(), filters);
        assertEquals(expectedProjects, filteredProjects.toList());
    }
}
