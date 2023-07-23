package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;

    @Test
    void testCrateSubProject() {
        CreateSubProjectDto createSubProjectDto = CreateSubProjectDto.builder()
                .name("SubProject")
                .visibility(ProjectVisibility.PRIVATE)
                .parentId(1L)
                .build();

        ProjectDto subProjectDto = ProjectDto.builder()
                .name("SubProject")
                .visibility(ProjectVisibility.PRIVATE)
                .parentId(1L)
                .build();

        Project parentProject = Project.builder()
                .id(1L)
                .visibility(ProjectVisibility.PRIVATE)
                .children(new ArrayList<>())
                .build();

        when(projectRepository.existsById(createSubProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(createSubProjectDto.getParentId())).thenReturn(parentProject);
        when(projectMapper.toProjectDto(createSubProjectDto)).thenReturn(subProjectDto);
        when(projectMapper.toEntity(subProjectDto)).thenReturn(new Project());
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());
        when(projectMapper.toDto(any(Project.class))).thenReturn(subProjectDto);

        ProjectDto projectDtoActual = projectService.createSubProject(createSubProjectDto);

        assertNotNull(projectDtoActual);
        assertEquals(subProjectDto.getId(), projectDtoActual.getId());
        assertEquals(subProjectDto.getName(), projectDtoActual.getName());
        assertEquals(subProjectDto.getVisibility(), projectDtoActual.getVisibility());
    }

    @Test
    void testValidateParentProjectExists() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();

        when(projectRepository.existsById(createSubProjectDto.getParentId())).thenReturn(false);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(createSubProjectDto));
        assertEquals("No such parent project", validationException.getMessage());
    }

    @Test
    void testValidateVisibilityConsistency() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
        createSubProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.existsById(createSubProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(createSubProjectDto.getParentId())).thenReturn(parentProject);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(createSubProjectDto));
        assertEquals("The visibility of the subproject must be - " +
                parentProject.getVisibility() + " like the parent project", validationException.getMessage());
    }

    @Test
    void testValidateSubProjectUnique() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto();
        createSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        createSubProjectDto.setName("SubProject");

        Project childProject = new Project();
        childProject.setName("SubProject");

        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setChildren(List.of(childProject));

        when(projectRepository.existsById(createSubProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(createSubProjectDto.getParentId())).thenReturn(parentProject);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(createSubProjectDto));
        assertEquals("Subproject with name " + createSubProjectDto.getName() + " already exists",
                validationException.getMessage());
    }
}