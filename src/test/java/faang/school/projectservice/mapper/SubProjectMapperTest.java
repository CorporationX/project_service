package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubProjectMapperTest {
    private SubProjectMapper subProjectMapper = new SubProjectMapperImpl();
    private Project project;
    private Project projectFromDto;
    private Project parentProject;
    private List<Project> children;
    private SubProjectDto subProjectDto;

    @BeforeEach
    void setUp() {
        parentProject = Project.builder().id(1L).build();
        children = List.of(Project.builder().id(3L).build(), Project.builder().id(4L).build(),Project.builder().id(5L).build());
        project = Project.builder().id(2L).name("project").description("desc").parentProject(parentProject)
                .children(children).status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PUBLIC).build();
        subProjectDto = SubProjectDto.builder().id(2L).name("project").description("desc").parentId(1L)
                .childrenId(List.of(3L,4L,5L)).status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PUBLIC).build();
        projectFromDto = Project.builder().id(2L).name("project").description("desc")
                .status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PUBLIC).build();
    }

    @Test
    public void testToDtoValid() {
        SubProjectDto result = subProjectMapper.toDto(project);
        assertEquals(subProjectDto, result);
    }

    @Test
    public void testToEntityValid() {
        Project result = subProjectMapper.toEntity(subProjectDto);
        assertEquals(projectFromDto, result);
    }

    @Test
    public void testUpdateValid() {
        SubProjectDto projectDtoUpdate = SubProjectDto.builder().id(2L).name("projectUpdate").description("descUpdate")
                .status(ProjectStatus.CANCELLED).visibility(ProjectVisibility.PRIVATE).build();
        Project projectUpdate = Project.builder().id(2L).name("projectUpdate").description("descUpdate")
                .parentProject(parentProject).children(children)
                .status(ProjectStatus.CANCELLED).visibility(ProjectVisibility.PRIVATE).build();
        subProjectMapper.updateFromDto(projectDtoUpdate, project);
        assertEquals(projectUpdate, project);
    }
}