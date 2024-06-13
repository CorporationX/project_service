package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectMapperTest {
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    private Project firstProject;
    private ProjectDto firstProjectDto;
    private ProjectDto secondProjectDto;

    @BeforeEach
    void init(){
        firstProject = Project.builder()
                .id(1L)
                .name("Project1")
                .status(ProjectStatus.CREATED)
                .ownerId(10L)
                .description("Project1")
                .createdAt(LocalDateTime.of(2004, Month.DECEMBER, 13, 2,2,2))
                .updatedAt(LocalDateTime.of(2024, Month.MAY, 16, 2,2,2))
                .build();

        firstProjectDto = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .status(ProjectStatus.CREATED)
                .ownerId(10L)
                .description("Project1")
                .createdAt(LocalDateTime.of(2004, Month.DECEMBER, 13, 2,2,2))
                .updatedAt(LocalDateTime.of(2024, Month.MAY, 16, 2,2,2))
                .build();

        secondProjectDto = ProjectDto.builder()
                .id(2L)
                .name("Project2")
                .status(ProjectStatus.CREATED)
                .ownerId(20L)
                .description("Project1")
                .createdAt(LocalDateTime.of(2014, Month.APRIL, 23, 3,3,3))
                .updatedAt(LocalDateTime.of(2034, Month.AUGUST, 14, 4,4,4))
                .build();
    }

    @Test
    public void testToDto(){
        ProjectDto result = mapper.toDto(firstProject);
        assertEquals(result, firstProjectDto);
    }

    @Test
    public void testToProject(){
        Project result = mapper.toProject(firstProjectDto);
        assertEquals(result, firstProject);
    }

    @Test
    public void testToDtos(){
        List<ProjectDto> result = mapper.toDtos(List.of(firstProject));
        assertEquals(1, result.size());
        assertEquals(result.get(0), firstProjectDto);
    }

    @Test
    public void testUpdate(){
        mapper.updateProject(secondProjectDto, firstProject);
        assertEquals(secondProjectDto.getId(), firstProject.getId());
        assertEquals(secondProjectDto.getName(), firstProject.getName());
        assertEquals(secondProjectDto.getDescription(), firstProject.getDescription());
        assertEquals(secondProjectDto.getCreatedAt(), firstProject.getCreatedAt());
        assertEquals(secondProjectDto.getOwnerId(), firstProject.getOwnerId());
        assertEquals(secondProjectDto.getStatus(), firstProject.getStatus());
    }
}
