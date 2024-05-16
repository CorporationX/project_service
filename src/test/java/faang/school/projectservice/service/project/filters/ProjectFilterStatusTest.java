package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class ProjectFilterStatusTest {

    ProjectFilterDto projectFilterDto;
    ProjectFilterStatus projectFilterStatus;
    ProjectDto first = new ProjectDto();
    ProjectDto second = new ProjectDto();
    Stream<ProjectDto> projectDtoStream;

    @BeforeEach
    void setUp(){
        projectFilterDto = new ProjectFilterDto();
        projectFilterStatus = new ProjectFilterStatus();
        projectFilterDto.setProjectStatus(ProjectStatus.IN_PROGRESS);
        first.setStatus(ProjectStatus.IN_PROGRESS);
        second.setStatus(ProjectStatus.COMPLETED);
        projectDtoStream = Stream.of(first, second);
    }

    @Test
    public void testIsApplicableFalse(){
        assertFalse(projectFilterStatus.isApplicable(projectFilterDto));
    }

    @Test
    public void testIsApplicableTrue(){
        projectFilterDto.setProjectStatus(ProjectStatus.IN_PROGRESS);
        assertTrue(projectFilterStatus.isApplicable(projectFilterDto));
    }

    @Test
    public void testFilter() {
        List<ProjectDto> actual = projectFilterStatus.filter(projectDtoStream, projectFilterDto).toList();
        assertEquals(actual, List.of(first));
    }
}