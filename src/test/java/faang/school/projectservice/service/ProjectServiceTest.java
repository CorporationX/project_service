package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper;


    @Test
    void create() {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setOwner(teamMember);
        projectDto.setName("crud");

        projectService.create(projectDto);
        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));
    }

    @Test
    void createWithException() {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setOwner(teamMember);
        projectDto.setName("crud");


        Mockito.when(projectRepository.existsByOwnerUserIdAndName(teamMember.getUserId(), projectDto.getName()))
                .thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
    }

    @Test
    void updateStatusAndDescription() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        Mockito.when(projectRepository.getProjectById(projectDto.getId()))
                .thenReturn(Project.builder().build());
        projectService.updateStatusAndDescription(projectDto, projectDto.getId());

        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));
    }

    @Test
    void getProjectByName() {
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setStatus(ProjectStatus.CREATED);
        projectFilterDto.setName("project");
        projectFilterDto.setVisibility(ProjectVisibility.PRIVATE);

        List<Project> projects = List.of(
                Project.builder().name("project").status(ProjectStatus.CREATED).visibility(ProjectVisibility.PRIVATE).build()
        );

        Mockito.when(projectRepository.findAll()).thenReturn(projects);

        Assertions.assertEquals(projectService.getProjectByName(projectFilterDto), projects);
    }

    @Test
    void getProjectByStatus() {
        ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setStatus(ProjectStatus.CREATED);
        projectFilterDto.setName("project");
        projectFilterDto.setVisibility(ProjectVisibility.PRIVATE);

        List<Project> projects = List.of(
                Project.builder().name("project").status(ProjectStatus.CREATED).visibility(ProjectVisibility.PRIVATE).build()
        );

        Mockito.when(projectRepository.findAll()).thenReturn(projects);

        Assertions.assertEquals(projects, projectService.getProjectByName(projectFilterDto));
    }

    @Test
    void getAllProjectsFromBD() {
        List<Project> allProjects = new ArrayList<>();
        Mockito.when(projectRepository.findAll())
                .thenReturn(allProjects);

        List<ProjectDto> list = new ArrayList<>();
        Assertions.assertEquals(list, projectService.getAllProjectsFromBD());
    }

    @Test
    void getProjectByIdFromBD() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        Mockito.when(projectRepository.getProjectById(projectDto.getId()))
                .thenReturn(Project.builder().build());

        Assertions.assertEquals(projectMapper.toEntity(projectDto), projectMapper.toEntity(projectService.getProjectByIdFromBD(projectDto)));
    }
}