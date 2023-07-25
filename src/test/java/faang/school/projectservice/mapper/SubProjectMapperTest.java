package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubProjectMapperTest {
    @Autowired
    private SubProjectMapper subProjectMapper;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        this.projectDto = ProjectDto.builder()
                .id(1L)
                .name("Faang")
                .description("This is Faang")
                .ownerId(1)
                .parentProjectId(5L)
                .childrenIds(List.of(2L))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .stagesId(List.of(1L))
                .build();
    }

    @Test
    void toDtoTest() {
        Project project = Project.builder()
                .id(1L)
                .name("Faang")
                .description("This is Faang")
                .ownerId(1L)
                .parentProject(Project.builder()
                        .id(5L)
                        .build())
                .children(List.of(Project.builder()
                        .id(2L)
                        .build()))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .stages(List.of(Stage.builder()
                        .stageId(1L)
                        .build()))
                .build();

        assertEquals(projectDto, subProjectMapper.toDto(project));
    }

    @Test
    void toEntityTest() {
        Project project = Project.builder()
                .id(1L)
                .name("Faang")
                .description("This is Faang")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        assertEquals(project, subProjectMapper.toEntity(projectDto));
    }
}