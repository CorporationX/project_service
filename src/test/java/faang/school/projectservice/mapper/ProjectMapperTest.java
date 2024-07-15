package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {

    private ProjectMapper projectMapper;

    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectMapper = Mappers.getMapper(ProjectMapper.class);

        project = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .children(List.of(Project.builder().id(2L).build()))
                .parentProject(Project.builder().id(1L).build())
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .childrenProjectIds(List.of(2L))
                .parentProjectId(1L)
                .build();
    }

    @Test
    void toDto_should_return_mapped_entity() {
        ProjectDto mappedDto = projectMapper.toDto(project);

        assertEquals(project.getName(), mappedDto.getName());
        assertEquals(project.getDescription(), mappedDto.getDescription());
        assertEquals(project.getChildren().get(0).getId(), mappedDto.getChildrenProjectIds().get(0));
    }

    @Test
    void toEntity_should_return_mapped_dto() {
        Project mappedEntity = projectMapper.toEntity(projectDto);

        assertEquals(projectDto.getName(), mappedEntity.getName());
        assertEquals(projectDto.getDescription(), mappedEntity.getDescription());
    }

    @Test
    void toDto_should_return_empty_list_when_children_is_null() {
        project.setChildren(null);

        ProjectDto mappedDto = projectMapper.toDto(project);

        assertTrue(mappedDto.getChildrenProjectIds().isEmpty());
    }

    @Test
    void toEntity_should_keep_parent_null_when_parent_id_is_null() {
        projectDto.setParentProjectId(null);

        Project mappedEntity = projectMapper.toEntity(projectDto);

        assertNull(mappedEntity.getParentProject());
    }

    @Test
    void update_should_remap_fields_that_differ() {
        ProjectUpdateDto updatedDto = ProjectUpdateDto.builder()
                .name("Updated Test Project")
                .description("Updated Test Project Description")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        var updatedProject = projectMapper.update(updatedDto, project);

        assertEquals(updatedDto.getName(), updatedProject.getName());
        assertEquals(updatedDto.getDescription(), updatedProject.getDescription());
        assertEquals(updatedDto.getStatus(), updatedProject.getStatus());
        assertEquals(updatedDto.getVisibility(), updatedProject.getVisibility());
    }
}