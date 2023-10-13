package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.exception.DataValidationException;
import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;
import faang.school.projectservice.filters.ProjectFilter;
import faang.school.projectservice.filters.ProjectFilterByName;
import faang.school.projectservice.filters.ProjectFilterByStatus;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Spy
    private ProjectMapperImpl projectMapper = new ProjectMapperImpl();
    private ProjectDto projectDto;
    private Project project;

    private TeamMember teamMemberCurrentUser;
    private Team teamWithCurrentUser;

    private Team team;

    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        teamMember = TeamMember.builder()
                .userId(2L)
                .build();
        team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();
        teamMemberCurrentUser = TeamMember.builder()
                .userId(1L)
                .build();
        teamWithCurrentUser = Team.builder()
                .teamMembers(List.of(teamMemberCurrentUser))
                .build();
        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .build();
        LocalDateTime now = LocalDateTime.now();
        project = Project.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(teamWithCurrentUser))
                .status(ProjectStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void createValidProject() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(false);

        Mockito.when(projectMapper.toEntity(projectDto)).thenReturn(project);

        projectService.create(projectDto);
        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));
    }

    @Test
    void createWithDataValidationException() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName()))
                .thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
    }

    @Test
    void updateStatusAndDescription() {
        Long id = projectDto.getId();
        Mockito.when(projectRepository.getProjectById(id))
                .thenReturn(project);

        Mockito.when(projectMapper.toDto(project))
                .thenReturn(projectDto);

        ProjectDto updatedProject = projectService.update(projectDto, id);

        Mockito.verify(projectRepository, Mockito.times(1))
                .save(project);

        assertEquals(projectDto.getDescription(), updatedProject.getDescription());
        assertEquals(projectDto.getStatus(), updatedProject.getStatus());
    }

    @Test
    void getAllProjects_EmptyList() {
        List<Project> allProjects = new ArrayList<>();
        Mockito.when(projectRepository.findAll())
                .thenReturn(allProjects);

        List<ProjectDto> list = new ArrayList<>();
        Assertions.assertEquals(list, projectService.getAllProject());
    }

    @Test
    void getAllProjects() {
        Project build = Project.builder().id(1L).build();
        List<Project> allProjects = List.of(build);
        Mockito.when(projectRepository.findAll())
                .thenReturn(allProjects);


        assertEquals(List.of(projectMapper.toDto(build)), projectService.getAllProject());
    }

    @Test
    void getProjectById() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        Project build = Project.builder().id(1L).build();
        Mockito.when(projectRepository.getProjectById(build.getId()))
                .thenReturn(build);

        assertEquals(projectDto, projectService.getProjectById(1L));
    }

    @Test
    void getProjectById_EntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(10L));
    }

    @Test
    void deleteProjectById() {
        projectService.deleteProjectById(1L);
        Mockito.verify(projectJpaRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteNotExistProjectById() {
        projectService.deleteProjectById(10L);
        Mockito.verify(projectJpaRepository, Mockito.times(1)).deleteById(10L);

        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(projectDto.getId()));
    }
}