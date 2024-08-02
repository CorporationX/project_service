package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectMapperTest {
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private long id = 100L;
    private long ownerId = 10L;
    private String name = "Project name";
    private String description = "Project description";
    private Project project = Project.builder()
            .id(id)
            .name(name)
            .description(description)
            .ownerId(ownerId)
            .build();
    private ProjectDto projectDto = ProjectDto.builder()
            .id(id)
            .name(name)
            .description(description)
            .ownerId(ownerId)
            .build();

    @Test
    void testToDto() {
        ProjectDto result = projectMapper.toDto(project);

        assertEquals(projectDto, result);
    }

    @Test
    void testToEntity() {
        Project result = projectMapper.toEntity(projectDto);

        assertEquals(project, result);
    }
}