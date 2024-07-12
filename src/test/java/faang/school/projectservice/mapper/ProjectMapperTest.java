package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
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
                .build();

        projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .childrenProjectIds(List.of(2L))
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
}