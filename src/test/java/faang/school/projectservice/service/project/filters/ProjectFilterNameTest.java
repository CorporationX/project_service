package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class ProjectFilterNameTest {

    ProjectFilterDto projectFilterDto;

    ProjectFilter projectFilterName;

    ProjectDto first = new ProjectDto();

    ProjectDto second = new ProjectDto();

    Stream<ProjectDto> projectDtoStream;

    @BeforeEach
    void setUp() {
        projectFilterDto = new ProjectFilterDto();
        projectFilterName = new ProjectFilterName();
        projectFilterDto.setName("Name");
        first.setName("Name");
        second.setName("Surname");
        projectDtoStream = Stream.of(first, second);
    }

    @Test
    public void testIsApplicableFalse() {
        assertFalse(projectFilterName.isApplicable(projectFilterDto));
    }

    @Test
    public void testIsApplicableTrue() {
        assertTrue(projectFilterName.isApplicable(projectFilterDto));
    }

    @Test
    public void testFilter() {
        List<ProjectDto> actual = projectFilterName.filter(projectDtoStream, projectFilterDto).toList();
        assertEquals(actual, List.of(first));
    }
}