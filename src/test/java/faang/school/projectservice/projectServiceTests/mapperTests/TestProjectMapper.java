package faang.school.projectservice.projectServiceTests.mapperTests;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.projectMapper.ProjectMapper;
import faang.school.projectservice.mapper.projectMapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestProjectMapper {
    private final ProjectMapper projectMapper = new ProjectMapperImpl();
    private ProjectCreateDto projectCreateDto;
    Project project;

    @BeforeEach
    public void init() {
        projectCreateDto = ProjectCreateDto.builder()
                .ownerId(1L)
                .name("New Project")
                .description("New Project")
                .build();
        project = Project.builder()
                .ownerId(1L)
                .name("New Project")
                .description("New Project")
                .build();
    }

    @Test
    public void mustReturnProjectFromProjectCreateDto() {
        Project desireProjects = Project.builder()
                .ownerId(1L)
                .name("New Project")
                .description("New Project")
                .build();

        Project receivedProjects = projectMapper.toEntityFromCreateDto(projectCreateDto);

        Assertions.assertEquals(receivedProjects, desireProjects);

    }

    @Test
    public void mustReturnProjectResponseDtoFromProject() {
        ProjectResponseDto desireResponseDto = ProjectResponseDto.builder()
                .ownerId(1L)
                .name("New Project")
                .description("New Project")
                .build();

        ProjectResponseDto receivedProjectResponseDto = projectMapper.toResponseDtoFromEntity(project);


        Assertions.assertEquals(receivedProjectResponseDto, desireResponseDto);
    }

    @Test
    public void testProjectResponseDtoConversionShouldReturnNonEqualObjects() {
        ProjectResponseDto desireResponseDto = ProjectResponseDto.builder()
                .ownerId(1L)
                .name("New Project")
                .build();

        ProjectResponseDto receivedProjectResponseDto = projectMapper.toResponseDtoFromEntity(project);

        Assertions.assertNotEquals(receivedProjectResponseDto, desireResponseDto);
    }
}
