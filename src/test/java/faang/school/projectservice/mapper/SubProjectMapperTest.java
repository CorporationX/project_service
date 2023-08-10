package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubProjectMapperTest {
    @Autowired
    private SubProjectMapper subProjectMapper;
    private SubProjectDto subProjectDto;

    @BeforeEach
    void setUp() {
        this.subProjectDto = SubProjectDto.builder()
                .id(1L)
                .name("Faang")
                .description("This is Faang")
                .ownerId(1)
                .parentProjectId(5L)
                .visibility(ProjectVisibility.PUBLIC)
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
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        assertEquals(subProjectDto, subProjectMapper.toDto(project));
    }

    @Test
    void toEntityTest() {
        Project project = Project.builder()
                .id(1L)
                .name("Faang")
                .description("This is Faang")
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        assertEquals(project, subProjectMapper.toEntity(subProjectDto));
    }
}