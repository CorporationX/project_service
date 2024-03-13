package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectNameFilterTest {

    @InjectMocks
    private ProjectNameFilter projectNameFilter;

    @Test
    void isApplicable_ValidArgs() {
        boolean expected = true;

        boolean actual = projectNameFilter.isApplicable(getFilters());

        assertEquals(expected, actual);
    }

    @Test
    void apply_ValidArgs() {
        List<Project> expected = Collections.singletonList(getProjects().get(1));

        List<Project> actual = getProjects();
        projectNameFilter.apply(actual, getFilters());

        assertEquals(expected, actual);
    }

    private ProjectFilterDto getFilters() {
        return ProjectFilterDto.builder()
                .namePattern("am")
                .build();
    }

    private List<Project> getProjects() {
        return new ArrayList<>(List.of(
                Project.builder()
                        .name("erghker")
                        .build(),
                Project.builder()
                        .name("ame")
                        .build()
        ));
    }
}
