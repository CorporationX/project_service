package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectStatusFilterTest {

    @InjectMocks
    private ProjectStatusFilter projectStatusFilter;

    @Test
    void isApplicable_ValidArgs() {
        boolean expected = true;

        boolean actual = projectStatusFilter.isApplicable(getFilters());

        assertEquals(expected, actual);
    }

    @Test
    void apply_ValidArgs() {
        List<Project> expected = Collections.singletonList(getProjects().get(0));

        List<Project> actual = getProjects();
        projectStatusFilter.apply(actual, getFilters());

        assertEquals(expected, actual);
    }

    private ProjectFilterDto getFilters() {
        return ProjectFilterDto.builder()
                .statusPattern(ProjectStatus.CANCELLED)
                .build();
    }

    private List<Project> getProjects() {
        return new ArrayList<>(List.of(
                Project.builder()
                        .status(ProjectStatus.CANCELLED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.CREATED)
                        .build()
        ));
    }
}
