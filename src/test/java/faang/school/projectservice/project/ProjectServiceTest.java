package faang.school.projectservice.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.project.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectJpaRepository projectJpaRepository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectMapperImpl projectMapper;
    @Mock
    private SubProjectMapperImpl subProjectMapper;
    @InjectMocks
    private ProjectService service;

    @Test
    public void testCheckDtoForNullFields() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setStagesIds(List.of(1L, 2L));
        projectDto.setTeamsIds(List.of(1L, 2L));
        when(projectMapper.toEntity(projectDto)).thenReturn(new Project());
        service.createProject(projectDto);
        verify(stageRepository, times(1)).findAllById(projectDto.getStagesIds());
        verify(teamRepository, times(1)).findAllById(projectDto.getTeamsIds());
    }

    @Test
    public void testCreateProject() {
        ProjectDto projectDto = new ProjectDto();
        Project project = new Project();
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        service.createProject(projectDto);
        verify(projectJpaRepository, times(1)).save(project);
    }

    @Test
    public void testCreateSubProject() {
        CreateSubProjectDto projectDto = new CreateSubProjectDto();
        Project parentProject = new Project();
        Project childProject = new Project();
        List<Project> childrenList = new ArrayList<>();
        parentProject.setId(1L);
        parentProject.setChildren(childrenList);
        when(projectJpaRepository.getReferenceById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);
        service.createSubProject(1L, projectDto);
        assertEquals(childProject.getParentProject(), parentProject);
        verify(projectJpaRepository, times(1)).save(parentProject);
        verify(projectJpaRepository, times(1)).save(childProject);
    }

    @Test
    public void testCreateSubProjectDifferentVisibility() {
        CreateSubProjectDto projectDto = new CreateSubProjectDto();
        Project parentProject = new Project();
        Project childProject = new Project();
        List<Project> childrenList = new ArrayList<>();
        parentProject.setId(1L);
        parentProject.setChildren(childrenList);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        childProject.setVisibility(ProjectVisibility.PRIVATE);
        when(projectJpaRepository.getReferenceById(any())).thenReturn(parentProject);
        when(subProjectMapper.toEntity(any())).thenReturn(childProject);
        assertThrows(DataValidationException.class, ()-> service.createSubProject(1L,projectDto));
    }
}
