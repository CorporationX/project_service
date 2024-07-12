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
    private long parentId = 1L;
    private long childrenId = 200L;
    private long ownerId = 10L;
    private String name = "Project name";
    private String description = "Project description";
    private Project parentProject = Project.builder()
            .id(parentId)
            .build();
    private Project childrenProject = Project.builder()
            .id(childrenId)
            .build();
    private Project project = Project.builder()
            .id(id)
            .name(name)
            .description(description)
            .ownerId(ownerId)
            .parentProject(parentProject)
            .children(List.of(childrenProject))
            .build();
    private ProjectDto childrenProjectDto = new ProjectDto();
    {
        childrenProjectDto.setId(childrenId);
    }
    private ProjectDto projectDto = new ProjectDto();
    {
        projectDto.setId(id);
        projectDto.setName(name);
        projectDto.setDescription(description);
        projectDto.setOwnerId(ownerId);
        projectDto.setParentProject(parentProject);
        projectDto.setChildren(List.of(childrenProjectDto));
    }
    @Test
    void testToDto() {
        ProjectDto result = projectMapper.toDto(project);

        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(parentProject, result.getParentProject());
        assertEquals(1, result.getChildren().size());
        assertEquals(childrenId, result.getChildren().get(0).getId());
    }

    @Test
    void testToEntity() {
        Project result = projectMapper.toEntity(projectDto);

        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(parentProject, result.getParentProject());
        assertEquals(1, result.getChildren().size());
        assertEquals(childrenId, result.getChildren().get(0).getId());
    }
}
