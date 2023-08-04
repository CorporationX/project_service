package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectMapperTest {

    ProjectMapper projectMapper = new ProjectMapperImpl();
    ProjectCreateDto projectCreateDto;
    Project project;

    @BeforeEach
    void setUp() {
        projectCreateDto = faang.school.projectservice.dto.project.ProjectCreateDto.builder()
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .build();
        project = Project.builder()
                .name("Project")
                .description("new Project")
                .status(ProjectStatus.CREATED)
                .ownerId(1L)
                .build();
    }

    @Test
    void testMapperToDto() {
        Assertions.assertEquals(projectCreateDto, projectMapper.toDto(project));
    }

    @Test
    void testMapperToModel() {
        Assertions.assertEquals(project, projectMapper.toModel(projectCreateDto));
    }
}