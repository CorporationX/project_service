package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
    private ProjectDto projectDto = new ProjectDto();
    {
        projectDto.setId(id);
        projectDto.setName(name);
        projectDto.setDescription(description);
        projectDto.setOwnerId(ownerId);
    }
    @Test
    void testToDto() {
        ProjectDto result = projectMapper.toDto(project);

        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ownerId, result.getOwnerId());
    }

    @Test
    void testToEntity() {
        Project result = projectMapper.toEntity(projectDto);

        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ownerId, result.getOwnerId());
    }
}
