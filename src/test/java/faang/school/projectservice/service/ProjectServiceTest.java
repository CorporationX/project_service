package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);;

    @Test
    void testCrateSubProject() {
        ProjectDto subProjectDto = ProjectDto.builder()
                .name("SubProject")
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project subProject = projectMapper.toEntity(subProjectDto);
        Project parentProject = Project.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .children(new ArrayList<>())
                .build();
        subProject.setParentProject(parentProject);
        ProjectDto subProjectDtoExpected = projectMapper.toDto(subProject);


        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getParentId())).thenReturn(parentProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);

        ProjectDto projectDtoActual = projectService.createSubProject(subProjectDto);

        assertNotNull(projectDtoActual);
        assertEquals(subProjectDtoExpected, projectDtoActual);
        assertEquals("SubProject", projectDtoActual.getName());
        assertEquals(ProjectVisibility.PRIVATE, projectDtoActual.getVisibility());
    }

    @Test
    void testValidateParentProjectExists() {
        ProjectDto subProjectDto = new ProjectDto();

        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(false);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(subProjectDto), "No such parent project");
    }

    @Test
    void testValidateVisibilityConsistency() {
        ProjectDto subProjectDto = new ProjectDto();
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getParentId())).thenReturn(parentProject);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(subProjectDto));
        assertEquals("The visibility of the subproject must be - " +
                parentProject.getVisibility() + " like the parent project", validationException.getMessage());
    }

    @Test
    void testValidateSubProjectUnique() {
        ProjectDto subProjectDto = new ProjectDto();
        subProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        subProjectDto.setName("SubProject");

        Project childProject = new Project();
        childProject.setName("SubProject");

        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setChildren(List.of(childProject));

        when(projectRepository.existsById(subProjectDto.getParentId())).thenReturn(true);
        when(projectRepository.getProjectById(subProjectDto.getParentId())).thenReturn(parentProject);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(subProjectDto));
        assertEquals("Subproject with name " + subProjectDto.getName() + " already exists",
                validationException.getMessage());
    }
}
