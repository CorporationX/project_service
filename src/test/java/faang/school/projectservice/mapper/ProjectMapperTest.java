package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ProjectMapperTest {
    private final ProjectMapperImpl projectMapper = new ProjectMapperImpl();
    private Project projectEntity;
    private ProjectDto projectDto;

    @BeforeEach
    void init() {
        long projectId = 1L;
        long projectOwnerId = 1L;
        String projectName = "project name";
        String projectDesc = "project description";
        ProjectStatus projectStatus = ProjectStatus.CREATED;
        ProjectVisibility projectVisibility = ProjectVisibility.PUBLIC;

        projectDto = new ProjectDto();
        projectDto.setId(projectId);
        projectDto.setOwnerId(projectOwnerId);
        projectDto.setName(projectName);
        projectDto.setDescription(projectDesc);
        projectDto.setStatus(projectStatus);
        projectDto.setVisibility(projectVisibility);

        projectEntity = new Project();
        projectEntity.setId(projectId);
        projectEntity.setOwnerId(projectOwnerId);
        projectEntity.setName(projectName);
        projectEntity.setDescription(projectDesc);
        projectEntity.setStatus(projectStatus);
        projectEntity.setVisibility(projectVisibility);
    }

    @Test
    void testToDto_argIsNull_returnsNull() {
        ProjectDto projectDtoByMapper = projectMapper.toDto(null);
        assertNull(projectDtoByMapper);
    }

    @Test
    void testToDto_filledEntity_returnsDto() {
        ProjectDto projectDtoByMapper = projectMapper.toDto(projectEntity);
        assertEquals(projectDto, projectDtoByMapper);
    }

    @Test
    void testToEntity_argIsNull_returnsNull() {
        Project projectEntityByMapper = projectMapper.toEntity(null);
        assertNull(projectEntityByMapper);
    }

    @Test
    void testToEntity_filledEntity_returnsDto() {
        Project projectEntityByMapper = projectMapper.toEntity(projectDto);
        assertEquals(projectEntity, projectEntityByMapper);
    }
}
