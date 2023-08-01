package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectFilterByNameTest {
    private ProjectFilterByName projectFilterByName;
    private ProjectFilterDto projectFilterDto;
    private Project projectWithId;
    private Stream<Project> projectStream;
    private Project first;
    private Project second;

    @BeforeEach
    void setUp() {
        this.projectFilterByName = new ProjectFilterByName();
        this.projectFilterDto = ProjectFilterDto.builder()
                .projectNamePattern("name")
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        this.projectWithId = Project.builder()
                .id(1L)
                .build();
        this.first = Project.builder()
                .id(1L)
                .name("My first name")
                .description("Some description")
                .ownerId(10L)
                .parentProject(projectWithId)
                .children(List.of(projectWithId))
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .stages(List.of(Stage.builder().stageId(15L).build()))
                .build();
        this.second = Project.builder()
                .id(2L)
                .name("My second name")
                .description("Some description")
                .ownerId(20L)
                .parentProject(projectWithId)
                .children(List.of(projectWithId))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .stages(List.of(Stage.builder().stageId(15L).build()))
                .build();
        this.projectStream = Stream.of(first, second);
    }

    @Test
    void isApplicableReturnFalse() {
        ProjectFilterDto nullNamePatternDto = ProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        boolean result = projectFilterByName.isApplicable(nullNamePatternDto);

        assertFalse(result);
    }

    @Test
    void isApplicableReturnTrue() {
        boolean result = projectFilterByName.isApplicable(projectFilterDto);

        assertTrue(result);
    }

    @Test
    void applyReturnFilteredStream() {
        first.setName("Wolf Among Us");
        List<Project> expected = List.of(second);

        Stream<Project> result = projectFilterByName.apply(projectStream, projectFilterDto);

        assertEquals(expected, result.toList());
    }
}