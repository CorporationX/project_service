package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.filter.ProjectFilter;
import faang.school.projectservice.service.filter.ProjectNameFilter;
import faang.school.projectservice.service.filter.ProjectStatusFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectJpaRepository projectJpaRepository;
    @Spy
    private ProjectMapper projectMapper;

    long requestUserId;
    ProjectDto projectDto, projectDto2, projectDto3, projectDto4;
    Project project, project2, project3, project4;
    ProjectStatus projectStatus;
    List<Project> projectList;
    List<ProjectDto> projectDtoList;

    @BeforeEach
    public void init() {
        requestUserId = 1L;
        projectStatus = ProjectStatus.CREATED;

        projectDto = new ProjectDto();
        projectDto.setId( 1L );
        projectDto.setName( "Project 1" );
        projectDto.setDescription( "Description of project 1" );
        projectDto.setVisibility( ProjectVisibility.PUBLIC );
        projectDto.setStatus( ProjectStatus.CREATED );
        projectDto.setOwnerId( requestUserId );

        project = Project.builder()
                .id( 1L )
                .name( "Project 1" )
                .description( "Description of project 1" )
                .visibility( ProjectVisibility.PUBLIC )
                .status( ProjectStatus.CREATED  )
                .ownerId( requestUserId )
                .build();


        projectDto2 = new ProjectDto();
        projectDto2.setId( 2L );
        projectDto2.setName( "Project 2" );
        projectDto2.setDescription( "Description of project 2" );
        projectDto2.setVisibility( ProjectVisibility.PRIVATE );
        projectDto2.setStatus( ProjectStatus.IN_PROGRESS );
        projectDto2.setOwnerId( requestUserId );

        project2 = Project.builder()
                .id( 2L )
                .name( "Project 2" )
                .description( "Description of project 2" )
                .visibility( ProjectVisibility.PRIVATE )
                .status( ProjectStatus.IN_PROGRESS  )
                .ownerId( requestUserId )
                .build();

        projectDto3 = new ProjectDto();
        projectDto3.setId( 3L );
        projectDto3.setName( "Project 3" );
        projectDto3.setDescription( "Description of project 3" );
        projectDto3.setVisibility( ProjectVisibility.PUBLIC );
        projectDto3.setStatus( ProjectStatus.COMPLETED );
        projectDto3.setOwnerId( requestUserId );

        project3 = Project.builder()
                .id( 3L )
                .name( "Project 3" )
                .description( "Description of project 3" )
                .visibility( ProjectVisibility.PRIVATE )
                .status( ProjectStatus.COMPLETED  )
                .ownerId( requestUserId )
                .build();

        projectDto4 = new ProjectDto();
        projectDto4.setId( 4L );
        projectDto4.setName( "Project 4" );
        projectDto4.setDescription( "Description of project 4" );
        projectDto4.setVisibility( ProjectVisibility.PRIVATE );
        projectDto4.setStatus( ProjectStatus.CREATED );
        projectDto4.setOwnerId( requestUserId );

        project4 = Project.builder()
                .id( 4L )
                .name( "Project 4" )
                .description( "Description of project 4" )
                .visibility( ProjectVisibility.PRIVATE )
                .status( ProjectStatus.CREATED  )
                .ownerId( requestUserId )
                .build();


        projectDtoList = List.of(projectDto, projectDto2, projectDto3, projectDto4);
        projectList = List.of(project, project2, project3, project4);
    }

    @Test
    public void testCreateProjectSuccess() {
        when(projectJpaRepository.existsByOwnerIdAndName( anyLong(), anyString() )).thenReturn( false );
        when(projectMapper.toEntity( any(ProjectDto.class), anyLong() )).thenReturn(project);
        when(projectJpaRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto( any(Project.class) )).thenReturn(projectDto );

        assertDoesNotThrow(() -> projectService.createProject(projectDto, requestUserId));

        verify(projectMapper, times(1)).toEntity(projectDto, requestUserId);
        verify(projectJpaRepository, times(1)).save(project);
        verify(projectMapper, times(1)).toDto(project);

    }

    @Test
    public void testUpdateProjectSuccess() {

        TeamMember teamMember = TeamMember.builder().id(1L).userId(requestUserId).build();
        Team team =Team.builder().id(1L).teamMembers(List.of(teamMember)).build();

        project.setTeams(List.of(team));

        when(projectJpaRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectJpaRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto( any(Project.class) )).thenReturn( projectDto );


        assertDoesNotThrow(() -> projectService.updateProject(1L, projectDto, requestUserId));

        verify(projectJpaRepository, times(1)).findById(1L);
        verify(projectMapper, times(1)).toDto(project);
        verify(projectJpaRepository, times(1)).save(project);
    }

    @Test
    public void testUpdateProjectIdDoesNotExist() {
        when(projectJpaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                projectService.updateProject(1L, projectDto, requestUserId));

        verify(projectJpaRepository).findById(1L);
        verifyNoInteractions(projectMapper);
        verify(projectJpaRepository, times( 0 )).save( any() );
    }

    @Test
    void findAllProjectsByFilters_noProjectsFound_throwsEntityNotFoundException() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .namePattern("project")
                .statusPattern(ProjectStatus.CREATED)
                .build();
        when(projectJpaRepository.findProjectByOwnerIdAndTeamMember(requestUserId)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> {
            projectService.findAllProjectsByFilters(filterDto, 1L);
        });
    }

    @Test
    public void testFindAllProjects() {
        when(projectJpaRepository.findProjectByOwnerIdAndTeamMember(requestUserId)).thenReturn(projectList);
        when(projectMapper.toDtoList(projectList)).thenReturn(projectDtoList);

        List<ProjectDto> projectsDto = projectService.getAllProjects(requestUserId);

        assertEquals(4, projectsDto.size());

        verify(projectJpaRepository, times(1)).findProjectByOwnerIdAndTeamMember(requestUserId);
        verify(projectMapper, times(1)).toDtoList(projectList);
    }

    @Test
    public void testFindProjectById() {
        when(projectJpaRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<Project> result = projectJpaRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(project.getId(), result.get().getId());
        verify(projectJpaRepository, times(1)).findById(1L);
    }
}
