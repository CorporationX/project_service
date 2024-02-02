package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {
    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private Project project;
    private ProjectDto projectDto;
    private Project createdProject;
    private CreateSubProjectDto createSubProjectDto;
    private Project parent;


    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .build();
        createdProject = Project.builder()
                .name("CreateSubProject")
                .description("Testing")
                .parentProject(parent)
                .build();
        createSubProjectDto = CreateSubProjectDto.builder()
                .name("CreateSubProject")
                .description("Testing")
                .parentId(111L)
                .build();
        parent = Project.builder()
                .id(111L)
                .parentProject(null)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    public void testToDto() {
        assertEquals(projectDto, projectMapper.toDto(project));
    }

    @Test
    public void testToEntity() {
        assertEquals(createdProject, projectMapper.toEntity(createSubProjectDto));
    }
}